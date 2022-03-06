package com.aikaload.service;

import com.aikaload.dto.Response;
import com.aikaload.entity.JobInfo;
import com.aikaload.entity.TransactionHistory;
import com.aikaload.entity.TruckInfo;
import com.aikaload.entity.UserAccount;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.pagination.PaginationUtil;
import com.aikaload.pagination.SqlQuery;
import com.aikaload.repo.*;
import com.aikaload.utils.VariableUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
@AllArgsConstructor
public class ReportService {

    private final TransactionHistoryRepo transactionHistoryRepo;
    private final UserAccountRepo userAccountRepo;
    private final UserService userService;
    private PaginationUtil paginationUtil;
    private TruckInfoRepo truckInfoRepo;
    private JobInfoRepo jobInfoRepo;
    private TruckService truckService;
    private JobService jobService;


    public ResponseEntity getTransactionHistoryByUserId(Long userId) {
        if(userId == null)
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "userId is invalid", userId));

        Optional<UserAccount> userAccount = userAccountRepo.findById(userId);
        if(!userAccount.isPresent())
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "userId is invalid", userId));

        List<TransactionHistory> transactionHistoryList = transactionHistoryRepo.findByUserId(userId);
        if(transactionHistoryList == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), "No record found for userId", userId));

        List<Object> results = new ArrayList<>();
        for(TransactionHistory transactionHistory:transactionHistoryList ){
        Map<String,Object> result = new HashMap<>();
        result.put("userInfo",userService.returnUserAccountInMapFormat(userAccount.get()));
        result.put("transactionType",transactionHistory.getTransactionType());
        result.put("transactionDate",transactionHistory.getTransactionDate());
        result.put("transRef",transactionHistory.getTransactionRef());
        result.put("narration",transactionHistory.getNarration());
        result.put("amount",transactionHistory.getAmount());
        results.add(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(ResponseEnum.OK.getCode(), "Successful", results));
    }



    public ResponseEntity getSettlementHistoryByTruckOwnerId(Long truckOwnerId){
        if(truckOwnerId == null)
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "truckOwnerId is invalid", truckOwnerId));

        Optional<UserAccount> userAccount = userAccountRepo.findById(truckOwnerId);
        if(!userAccount.isPresent())
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "truckOwnerId is invalid", truckOwnerId));

        if(!userAccount.get().getUserRole().getUserRoleName().equals(VariableUtil.TRUCK_OWNER))
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Only truck owners are allowed access to this feature", truckOwnerId));

        List<Object[]> resultSet = paginationUtil.executeQuery(String.format(SqlQuery.SETTLEMENT_HISTORY_QUERY,truckOwnerId));
        if(resultSet ==  null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), "No record found for truckOwnerId", truckOwnerId));

       List<Object> results = new ArrayList<>();
       for(Object[] r:   resultSet){
           Map<String,Object> result = new HashMap<>();
           result.put("completionCode",r[0]);
           result.put("loadCode",r[1]);
           int truckId = (int) r[2];
           Optional<TruckInfo> truckInfo = truckInfoRepo.findById(truckId);
           result.put("truckId",truckService.returnTruckInMapFormat(truckInfo.get(),null,VariableUtil.ORM));
           result.put("status",r[3]);
           result.put("createdDate",r[4]);
           BigInteger jobId = (BigInteger) r[5];
           Optional<JobInfo> jobInfo = jobInfoRepo.findById(jobId.longValue());
           result.put("jobId",jobService.returnJobInMapFormat(jobInfo.get(),null,VariableUtil.ORM));
           result.put("offerAmount",r[6]);
           results.add(result);
       }
        return ResponseEntity.status(HttpStatus.OK).body(new Response(ResponseEnum.OK.getCode(), "Successful", results));
    }
}
