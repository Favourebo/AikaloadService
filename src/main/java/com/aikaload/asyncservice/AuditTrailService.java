package com.aikaload.asyncservice;

import com.aikaload.entity.AuditTrail;
import com.aikaload.repo.AuditTrailRepo;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuditTrailService {
    private final AuditTrailRepo auditTrailRepo;

    @Async("executorB")
    public void saveAudit(AuditTrail auditTrail){
        try{
            auditTrailRepo.save(auditTrail);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
