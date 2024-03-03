package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user=userRepository2.findById(userId).get();
        if(user.getConnected()){
            throw new Exception("Already connected");
        }else if(countryName.equalsIgnoreCase(user.getOriginalCountry().getCountryName().toString())){
            return user;
        }else{
            if(user.getServiceProviderList().isEmpty()){
                throw new Exception("Unable to connect");
            }
            ServiceProvider serviceProvider=null;
            List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
            Country country1=null;

            for(ServiceProvider serviceProvider1:serviceProviderList){
                List<Country> countryList=serviceProvider1.getCountryList();
                for(Country country:countryList){
                    if(countryName.equalsIgnoreCase(country.getCountryName().toString())){
                        if(serviceProvider==null){
                            serviceProvider=serviceProvider1;
                        }else{
                            serviceProvider=serviceProvider.getId()<serviceProvider1.getId()?serviceProvider:serviceProvider1;
                        }
                        country1=country;
                    }
                }
            }
            if(serviceProvider==null){
                throw new Exception("Unable to connect");
            }
            Connection connection=new Connection();
            connection.setUser(user);
            connection.setServiceProvider(serviceProvider);


            String maskedIp=country1.getCode()+"."+serviceProvider.getId()+"."+userId;
            user.setMaskedIp(maskedIp);
            user.setConnected(true);
            user.getConnectionList().add(connection);

            serviceProvider.getConnectionList().add(connection);
            userRepository2.save(user);
            serviceProviderRepository2.save(serviceProvider);
            return user;
        }
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user=userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }
        user.setConnected(false);
        user.setMaskedIp(null);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender=userRepository2.findById(senderId).get();
        User receiver=userRepository2.findById(receiverId).get();

        if(receiver.getConnected()){
            String ip=receiver.getMaskedIp();
            String code=ip.substring(0,3);
            if(code.equalsIgnoreCase(sender.getOriginalCountry().getCode())){
                return sender;
            }else {
                String countryName = "";
                if (code.equals(CountryName.IND.toCode())) {
                    countryName = CountryName.IND.toString();
                }
                if (code.equals(CountryName.USA.toCode())) {
                    countryName = CountryName.USA.toString();
                }
                if (code.equals(CountryName.AUS.toCode())) {
                    countryName = CountryName.AUS.toString();
                }
                if (code.equals(CountryName.CHI.toCode())) {
                    countryName = CountryName.CHI.toString();
                }
                if (code.equals(CountryName.JPN.toCode())) {
                    countryName = CountryName.JPN.toString();
                }
                try {
                    return connect(senderId, countryName);
                } catch (Exception e) {
                    throw new Exception("Cannot establish communication");
                }
            }

        }else{
            if(sender.getOriginalCountry().equals(receiver.getOriginalCountry())){
                return sender;
            }else{
                try{
                    return connect(senderId,receiver.getOriginalCountry().getCountryName().toString());
                }catch (Exception e){
                    throw new Exception("Cannot establish communication");
                }
            }
        }
    }
}
