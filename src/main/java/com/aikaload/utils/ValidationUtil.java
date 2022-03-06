package com.aikaload.utils;


import com.aikaload.dto.*;
import com.aikaload.entity.AssignJobInfo;
import com.aikaload.entity.JobInfo;
import com.aikaload.entity.UserAccount;
import com.aikaload.entity.VerificationHistory;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.enums.VerificationHistoryEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Log4j2
public class ValidationUtil {

    private static boolean isValueEmpty(String param) {
        return param == null || param.isEmpty();
    }


    private static boolean isDigits(String value) {
        return !isValueEmpty(value) && (!value.isEmpty() && value.matches("^\\d*$"));
    }

    public static boolean validateEmailAddress(String emailAddress) {
        try {
            String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";
            return emailAddress.matches(EMAIL_REGEX);
        }catch(Exception e){
            return false;
        }
    }

    public ResponseEntity<Object> showInterestChecks(ShowInterestRequest showInterestRequest, VerificationHistory verificationHistory, Optional<UserAccount> userAccount, AssignJobInfo assignJobInfoExistRsp){
        /*if(verificationHistory == null)
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Only a verified user can indicate interest for a job!", showInterestRequest));

       if(verificationHistory.getVerificationStatus() == VerificationHistoryEnum.NOT_VERIFIED.getCode())
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Only a verified user can indicate interest for a job!", showInterestRequest));
*/
        if(!userAccount.get().getUserRole().getUserRoleName().equals(VariableUtil.TRUCK_OWNER))
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Only a truck owner is allowed to show interest in a job!", showInterestRequest));

        if(assignJobInfoExistRsp != null)
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "You have already indicated interest in this job!", showInterestRequest));

        return null;
    }




    public static String validateReviewRequest(CloseTaskRequest reviewRequest) {
        if(reviewRequest == null){
            return "Request cannot be empty";
        }
        return null;
    }

    public static String validateCreateJobRequest(CreateJobRequest jobRequest) {
        if(jobRequest == null) {
            //Terminate request and return response back to calling client
            log.error("<<<<<Job request cannot be empty");
            return "Request cannot be empty";
        }
        return null;
    }

    public static String validateEditJobRequest(EditJobRequest jobRequest) {
        if(jobRequest == null) {
            //Terminate request and return response back to calling client
            log.error("<<<<<Job request cannot be empty");
            return "Request cannot be empty";
        }
        return null;
    }

    public static boolean validateUserRightsOnJobEdit(UserAccount userAccount, JobInfo jobInfo, Long requestorId) {
        if(userAccount.getUserRole().getUserRoleName().startsWith(VariableUtil.ADMIN)  || requestorId == jobInfo.getUserAccount().getId()) {
            return true;
        }
        return false;
    }




    public static String validateReportUser(ReportUserRequest reportUserRequest) {
        if(reportUserRequest == null){
            //Terminate request and return response back to calling client
            log.error("<<<<<Report User Request cannot be empty");
            return "Request cannot be empty";
        }
        return null;
    }

    public static String validateFundWalletRequest(WalletRequest walletRequest){
        if(walletRequest == null){
            //Terminate request and return response back to calling client
            log.error("<<<<<Fund Wallet Request cannot be empty");
            return "Request cannot be empty";
        }
        return null;
    }

    public static String validateEditSetting(EditSettingRequest editSettingRequest) {
        if(editSettingRequest == null){
            //Terminate request and return response back to calling client
            log.error("<<<<<Edit Setting Request cannot be empty");
            return "Request cannot be empty";
        }
        return null;
    }
}
