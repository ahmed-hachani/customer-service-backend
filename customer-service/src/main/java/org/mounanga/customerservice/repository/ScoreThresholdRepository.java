package org.mounanga.customerservice.repository;

import org.mounanga.customerservice.entity.ScoreThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreThresholdRepository extends JpaRepository<ScoreThreshold,Long> {
}
