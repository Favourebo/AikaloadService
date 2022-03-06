package com.aikaload.repo;

import com.aikaload.entity.VerificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationHistoryRepo extends JpaRepository<VerificationHistory,Long>{
    VerificationHistory findByUserId(Long userId);
}
