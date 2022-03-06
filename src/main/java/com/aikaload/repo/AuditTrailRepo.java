package com.aikaload.repo;

import com.aikaload.entity.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailRepo extends JpaRepository<AuditTrail,Long> {
}
