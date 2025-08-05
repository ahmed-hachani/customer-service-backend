package org.mounanga.customerservice.service.implementation;

import org.mounanga.customerservice.entity.InterestRateSetting;
import org.mounanga.customerservice.repository.InterestRateSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InterestRateSettingService {

    @Autowired
    private InterestRateSettingRepository repo;

    public double getCurrentRate() {
        return repo.findAll().stream().findFirst().map(InterestRateSetting::getDefaultRate).orElse(4.0);
    }

    public InterestRateSetting updateRate(double rate) {
        InterestRateSetting setting = repo.findAll().stream().findFirst().orElse(new InterestRateSetting());
        setting.setDefaultRate(rate);
        return repo.save(setting);
    }
}

