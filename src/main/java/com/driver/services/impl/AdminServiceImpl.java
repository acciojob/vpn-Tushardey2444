package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin=new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Optional<Admin> admin=adminRepository1.findById(adminId);
        if(admin.isPresent()){
            Admin admin1=admin.get();
            ServiceProvider serviceProvider=new ServiceProvider();
            serviceProvider.setName(providerName);
            serviceProvider.setAdmin(admin1);

            admin1.getServiceProviders().add(serviceProvider);
            adminRepository1.save(admin1);
            return admin1;
        }
        return null;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        Optional<ServiceProvider> serviceProvider=serviceProviderRepository1.findById(serviceProviderId);
        if(serviceProvider.isPresent()){
            ServiceProvider serviceProvider1=serviceProvider.get();
            if(countryName.equalsIgnoreCase("ind") ||
                    countryName.equalsIgnoreCase("aus") ||
                    countryName.equalsIgnoreCase("usa") ||
                    countryName.equalsIgnoreCase("chi") ||
                    countryName.equalsIgnoreCase("jpn")){

                Country country=new Country();

                if(countryName.equalsIgnoreCase("ind")){
                    country.setCountryName(CountryName.IND);
                    country.setCode(CountryName.IND.toCode());
                }
                if(countryName.equalsIgnoreCase("aus")){
                    country.setCountryName(CountryName.AUS);
                    country.setCode(CountryName.AUS.toCode());
                }
                if(countryName.equalsIgnoreCase("usa")){
                    country.setCountryName(CountryName.USA);
                    country.setCode(CountryName.USA.toCode());
                }
                if(countryName.equalsIgnoreCase("chi")){
                    country.setCountryName(CountryName.CHI);
                    country.setCode(CountryName.CHI.toCode());
                }
                if(countryName.equalsIgnoreCase("jpn")){
                    country.setCountryName(CountryName.JPN);
                    country.setCode(CountryName.JPN.toCode());
                }

                country.setServiceProvider(serviceProvider1);
                serviceProvider1.getCountryList().add(country);
                serviceProviderRepository1.save(serviceProvider1);
                return serviceProvider1;
            }else{
                throw new Exception("Country not found");
            }
        }
        return null;
    }
}
