package com.aikaload.service;

import com.aikaload.dto.Response;
import com.aikaload.entity.FeeConfiguration;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.repo.FeeConfigurationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeeConfigurationService {

    private final FeeConfigurationRepo feeConfigurationRepo;

    public BigDecimal getVerificationCost(){
        Optional<FeeConfiguration> feeConfiguration = feeConfigurationRepo.findById(1);
        return feeConfiguration.get().getVerificationCost();
    }

    public BigDecimal getSmsCost(){
        Optional<FeeConfiguration> feeConfiguration = feeConfigurationRepo.findById(1);
        return feeConfiguration.get().getSmsCost();
    }

    public ResponseEntity<Object> editVerificationCost(BigDecimal newVerificationCost) {
        Optional<FeeConfiguration> feeConfiguration = feeConfigurationRepo.findById(1);
        if(feeConfiguration.isPresent()) {
            feeConfiguration.get().setVerificationCost(newVerificationCost);
            feeConfigurationRepo.save(feeConfiguration.get());
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),newVerificationCost));
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(),ResponseEnum.OPERATION_FAILED.getMessage(),newVerificationCost));
    }
}
