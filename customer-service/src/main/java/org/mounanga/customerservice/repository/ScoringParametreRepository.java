package org.mounanga.customerservice.repository;

import org.mounanga.customerservice.entity.ScoringParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoringParametreRepository extends JpaRepository<ScoringParameter,Long> {
    List<ScoringParameter> findByCriterion(String criterion);

}
