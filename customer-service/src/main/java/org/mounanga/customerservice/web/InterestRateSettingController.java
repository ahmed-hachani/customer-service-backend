package org.mounanga.customerservice.web;

import org.mounanga.customerservice.entity.InterestRateSetting;
import org.mounanga.customerservice.service.implementation.InterestRateSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequestMapping("/api/interest-rate")

public class InterestRateSettingController {
    @Autowired
    private InterestRateSettingService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")  
    @GetMapping
    public double getInterestRate() {
        return service.getCurrentRate();
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping
    public InterestRateSetting updateInterestRate(@RequestParam double rate) {
        return service.updateRate(rate);
    }
}
