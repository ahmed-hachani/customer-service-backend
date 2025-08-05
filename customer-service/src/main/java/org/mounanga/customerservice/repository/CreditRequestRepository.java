package org.mounanga.customerservice.repository;

import org.mounanga.customerservice.entity.CreditRequest;
import org.mounanga.customerservice.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CreditRequestRepository extends JpaRepository<CreditRequest,Long> {
    List<CreditRequest> findByStatus(Status status);
    List<CreditRequest> findByUserId(String userId);
    @Query(value = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS count " +
            "FROM credit_request GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> countRequestsPerMonth();
    @Query("SELECT cr.status, COUNT(cr) FROM CreditRequest cr GROUP BY cr.status")
    List<Object[]> countByApprovalStatus();
    @Query("SELECT cr.finalDecision, COUNT(cr) FROM CreditRequest cr GROUP BY cr.finalDecision")
    List<Object[]> countByFinalDecision();


}
