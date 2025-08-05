package org.mounanga.customerservice.repository;

import org.mounanga.customerservice.entity.InterestRateSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRateSettingRepository extends JpaRepository<InterestRateSetting, Long> {
}

