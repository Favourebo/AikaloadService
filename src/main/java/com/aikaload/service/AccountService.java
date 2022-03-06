package com.aikaload.service;

import com.aikaload.asyncservice.MailService;
import com.aikaload.asyncservice.SmsService;
import com.aikaload.dto.*;
import com.aikaload.entity.*;
import com.aikaload.enums.*;
import com.aikaload.repo.*;
import com.aikaload.utils.ValidationUtil;
import com.aikaload.utils.VariableUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;


@Log4j2
@Service("AccountService")
@AllArgsConstructor
public class AccountService {

    private final UserAccountRepo userAccountRepo;
    private final TransactionHistoryRepo transactionHistoryRepo;
    private final VerificationHistoryRepo verificationHistoryRepo;
    private final LoadCompletionCodeManagerRepo loadCompletionCodeManagerRepo;
    private final AssignJobInfoRepo assignJobInfoRepo;
    private final JobInfoRepo jobInfoRepo;
    private final TruckInfoRepo truckInfoRepo;
    private final Environment env;
    private final MailService mailService;
    private final SmsService smsService;

    public ResponseEntity editUserVerificationStatus(Long userId, boolean verificationStatus) {
        try {
            VerificationHistory verificationHistory = verificationHistoryRepo.findByUserId(userId);
            Optional<UserAccount> userAccountOptional = userAccountRepo.findById(userId);

            if (verificationHistory == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "No record was found for this userId", null));

            if (verificationStatus == true) {
                verificationHistory.setVerificationStatus(VerificationHistoryEnum.VERIFIED.getCode());
                userAccountOptional.get().setAccountVerified(true);
            }

            if (verificationStatus == false) {
                verificationHistory.setVerificationStatus(VerificationHistoryEnum.NOT_VERIFIED.getCode());
                userAccountOptional.get().setAccountVerified(false);
            }

            verificationHistoryRepo.save(verificationHistory);
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), verificationStatus));
        } catch (Exception e) {
            log.error("An error occurred while trying to edit verification status:::e:::{}", e);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), null));
    }

    public ResponseEntity getUserVerificationStatus(Long userId) {
        try{
            VerificationHistory verificationHistory = verificationHistoryRepo.findByUserId(userId);

            if(verificationHistory == null )
                 return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), VerificationHistoryEnum.NOT_VERIFIED.getMessage()));

            if(verificationHistory.getVerificationStatus() == VerificationHistoryEnum.PENDING_VERIFICATION.getCode())
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), VerificationHistoryEnum.PENDING_VERIFICATION.getMessage()));

            if(verificationHistory.getVerificationStatus() == VerificationHistoryEnum.VERIFIED.getCode())
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), VerificationHistoryEnum.VERIFIED.getMessage()));

            if(verificationHistory.getVerificationStatus() == VerificationHistoryEnum.NOT_VERIFIED.getCode())
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), VerificationHistoryEnum.NOT_VERIFIED.getMessage()));

         }catch(Exception e){
            log.error("An error occurred while trying to send verification message:::e:::{}",e);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), null));
    }


    public ResponseEntity sendVerificationMessage(Long userId) {
     try {
         VerificationHistory verificationHistory = null;

         //Validate userId
         Optional<UserAccount> userAccount = userAccountRepo.findById(userId);
         if (!userAccount.isPresent())
             return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId", userId));

          verificationHistory = verificationHistoryRepo.findByUserId(userId);
         //Validate if user has sent verification message before
         if(verificationHistory != null) {
             if(verificationHistory.getVerificationStatus() == VerificationHistoryEnum.NOT_VERIFIED.getCode()){
                 verificationHistory.setVerificationStatus(VerificationHistoryEnum.PENDING_VERIFICATION.getCode());
             }else {
                 return ResponseEntity.badRequest().body(new Response(ResponseEnum.DUPLICATE_ENTRY.getCode(), "userId has already sent a verification message before", userId));
             }
         }else {
             //create verification history object
             verificationHistory = new VerificationHistory();
             verificationHistory.setCreatedDate(new Date());
             verificationHistory.setUserId(userId);
             verificationHistory.setVerificationStatus(VerificationHistoryEnum.PENDING_VERIFICATION.getCode());
         }
         verificationHistoryRepo.save(verificationHistory);

         //Send Admin Verification email

         return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Verification message sent successfully", userId));
     }catch(Exception e){
         log.error("An error occurred while trying to send verification message:::e:::{}",e);
         return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), userId));
     }
    }



    @Transactional
    public ResponseEntity fundWallet(WalletRequest walletRequest) {
        try {
            String validationRsp = ValidationUtil.validateFundWalletRequest(walletRequest);

            if (validationRsp != null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), validationRsp, walletRequest));

            //Get User Account
            Optional<UserAccount> userAccount = userAccountRepo.findById(walletRequest.getUserId());
            if (!userAccount.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId sent!", walletRequest));

            //If account has never been credited before
            if (userAccount.get().getWalletBalance() == null)
                userAccount.get().setWalletBalance(walletRequest.getTransactionAmount());

                //If account has been credited before
            else
                userAccount.get().setWalletBalance(userAccount.get().getWalletBalance().add(walletRequest.getTransactionAmount()));

            UserAccount saveRsp = userAccountRepo.save(userAccount.get());

            if (saveRsp != null) {
                log.info("<<<<<Wallet funded successfully for transaction-ref::::{}", walletRequest.getTransactionReference());
                transactionHistoryRepo.save(populateTransactionHistory(walletRequest,TransactionTypeEnum.CREDIT.getMessage()));

                String msg = String.format("%s (%s) just made a payment of %s to his wallet",userAccount.get().getCompanyName(),
                        userAccount.get().getUsername(),walletRequest.getTransactionAmount());
                smsService.sendAdminPushNotification(msg,VariableUtil.FUND_WALLET_SLACK_URL);

                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Wallet was funded successfully", walletRequest));
            }
        } catch (Exception e) {
            log.error("<<<<An error occurred while trying to fund wallet:::error::{}", e);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), walletRequest));
    }


    @Transactional
    public ResponseEntity debitWallet(WalletRequest walletRequest) {
        try {
            String validationRsp = ValidationUtil.validateFundWalletRequest(walletRequest);

            if (validationRsp != null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), validationRsp, walletRequest));

            //Get User Account
            Optional<UserAccount> userAccount = userAccountRepo.findById(walletRequest.getUserId());
            if (!userAccount.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId sent!", walletRequest));

            //If account has never been credited before
            if (userAccount.get().getWalletBalance() == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INSUFFICIENT_BALANCE.getCode(),ResponseEnum.INSUFFICIENT_BALANCE.getMessage(), walletRequest));

           //If account has been credited before
            else {
                BigDecimal walletBalance = userAccount.get().getWalletBalance().subtract(walletRequest.getTransactionAmount());
                if(walletBalance.doubleValue() < 0)
                    return ResponseEntity.badRequest().body(new Response(ResponseEnum.INSUFFICIENT_BALANCE.getCode(),ResponseEnum.INSUFFICIENT_BALANCE.getMessage(), walletRequest));

              userAccount.get().setWalletBalance(walletBalance);
            }

            UserAccount saveRsp = userAccountRepo.save(userAccount.get());

            if (saveRsp != null) {
                log.info("<<<<<Wallet debited successfully for transaction-ref::::{}", walletRequest.getTransactionReference());
                transactionHistoryRepo.save(populateTransactionHistory(walletRequest,TransactionTypeEnum.DEBIT.getMessage()));
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Wallet was debited successfully", walletRequest));
            }
        } catch (Exception e) {
            log.error("<<<<An error occurred while trying to fund wallet:::error::{}", e);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), walletRequest));
    }


    /**
     * This helps handles edit settings
     *
     * @param editSettingRequest contains setting information
     * @return ResponseEntity contains response information
     */
    public ResponseEntity editSetting(EditSettingRequest editSettingRequest) {
        try {

            //Validate incoming request
            String validationRsp = ValidationUtil.validateEditSetting(editSettingRequest);
            if (validationRsp != null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), validationRsp, editSettingRequest));

            //Get Edit Setting
            Optional<UserAccount> userAccount = userAccountRepo.findById(editSettingRequest.getUserId());
            if (!userAccount.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId sent!", editSettingRequest));


            //Setting SMS Notification
            if (editSettingRequest.isSmsNotification()) {
                if (isBalanceValidForNotification(userAccount.get().getWalletBalance()))
                    userAccount.get().setSmsNotification(editSettingRequest.isSmsNotification());
                else
                    return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Kindly fund wallet to receive sms notification!", editSettingRequest));
            } else userAccount.get().setSmsNotification(editSettingRequest.isSmsNotification());

            //Setting Whatsapp Notification
            if (editSettingRequest.isWhatsappNotification()) {
                if (isBalanceValidForNotification(userAccount.get().getWalletBalance())) {
                    //Debit user N1000.00
                    ResponseEntity<Object> debitRsp = debitWallet(new WalletRequest(new BigDecimal("1000"),String.format("Whatsapp-Notification-charge/%s",userAccount.get().getMobilePhone()),userAccount.get().getId()));
                   if(debitRsp.getStatusCode() == HttpStatus.OK) {
                       userAccount.get().setWhatsappNotification(editSettingRequest.isWhatsappNotification());
                   }else{
                       return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Kindly fund wallet to receive whatsapp notification!", editSettingRequest));
                   }
                }

                else
                    return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Kindly fund wallet to receive whatsapp notification!", editSettingRequest));
            } else userAccount.get().setWhatsappNotification(editSettingRequest.isWhatsappNotification());


            //Setting Email Notification
            userAccount.get().setShowPhoneNumber(editSettingRequest.isShowPhoneNumber());
            userAccount.get().setShowWhatsappNumber(editSettingRequest.isShowWhatsappNumber());
            userAccount.get().setEmailNotification(editSettingRequest.isEmailNotification());
            userAccountRepo.save(userAccount.get());

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Settings changed successfully", editSettingRequest));
        } catch (Exception e) {
            log.error("<<<<<An error occurred while trying to edit setting:::error::{}", e);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), editSettingRequest));
    }


    private boolean isBalanceValidForNotification(BigDecimal customerBalance) {
        if (customerBalance.compareTo(new BigDecimal(VariableUtil.MIN_BALANCE)) < 0) return false;
        return true;
    }


    /**
     * This method helps populate transactionHistory
     *
     * @param walletRequest contains
     * @return
     */
    private static TransactionHistory populateTransactionHistory(WalletRequest walletRequest,String transactionType) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setAmount(walletRequest.getTransactionAmount());
        transactionHistory.setNarration(String.format("%s%s%s", transactionType, VariableUtil.DEFAULT_NARRATION, walletRequest.getTransactionReference()));
        transactionHistory.setTransactionDate(new Date());
        transactionHistory.setTransactionRef(walletRequest.getTransactionReference());
        transactionHistory.setTransactionType(transactionType);
        transactionHistory.setUserId(walletRequest.getUserId());
        return transactionHistory;
    }

    /**
     * This method helps to request settlement
     *
     * @param settlementRequest contains settlement information
     * @return
     */
    public ResponseEntity settlementRequest(SettlementRequest settlementRequest) {
         try{
           Optional<UserAccount> userAccount = userAccountRepo.findById(settlementRequest.getTruckOwnerId());
           Optional<JobInfo> jobInfo = jobInfoRepo.findById(settlementRequest.getJobId());


          if(StringUtils.isNotBlank(settlementRequest.getLoadCode())) {
              LoadCompletionCodeManager loadCompletionCodeManager = loadCompletionCodeManagerRepo.findByLoadCode(settlementRequest.getLoadCode());
              AssignJobInfo assignJobInfo = assignJobInfoRepo.findByAssignedToAndJobInfo(userAccount.get(),jobInfo.get());
              if(assignJobInfo == null)
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"Invalid truckOwnerId or jobId",settlementRequest));

              if(!loadCompletionCodeManager.getAssignJobId().equals(assignJobInfo.getId())) {
                  log.info("lccId:{}::::::assignJobInfoId::::{}",loadCompletionCodeManager.getAssignJobId(),assignJobInfo.getId());
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "You have no assigned job for this code", settlementRequest));
              }
              if(loadCompletionCodeManager.getStatus().equals(SettlementEnum.START_SETTLEMENT_REQUEST.getMessage()))
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.ALREADY_USED.getCode(),"Load Code has already been used",settlementRequest));

              loadCompletionCodeManager.setStatus(SettlementEnum.START_SETTLEMENT_REQUEST.getMessage());
              loadCompletionCodeManager.setAdminStatus(AdminSettlementStatusEnum.STARTED.getMessage());
              loadCompletionCodeManagerRepo.save(loadCompletionCodeManager);
              mailService.sendSettlementEmailToAdmin(env.getProperty("username"),userAccount.get(),SettlementTypeEnum.LOAD_CODE.getMessage(),loadCompletionCodeManager.getLoadCode());

              String msg = String.format("%s (%s) just successfully used his LOAD CODE to be paid %s for &s",userAccount.get().getCompanyName(),
                      userAccount.get().getUsername(),assignJobInfo.getOfferAmount(),jobInfo.get().getJobReferenceNumber());
              smsService.sendAdminPushNotification(msg,VariableUtil.LOAD_CODE_SLACK_URL);

              //Send Start Settlement Mail to Admin
              return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),"Settlement request was successful",settlementRequest));
          }



          if(StringUtils.isNotBlank(settlementRequest.getCompletionCode())) {
              LoadCompletionCodeManager loadCompletionCodeManager = loadCompletionCodeManagerRepo.findByCompletionCode(settlementRequest.getCompletionCode());
              AssignJobInfo assignJobInfo = assignJobInfoRepo.findByAssignedToAndJobInfo(userAccount.get(),jobInfo.get());
              if(assignJobInfo == null)
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"Invalid truckOwnerId or jobId",settlementRequest));

              if(!loadCompletionCodeManager.getAssignJobId().equals(assignJobInfo.getId()))
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"You have no assigned job for this code",settlementRequest));

              if(loadCompletionCodeManager.getStatus().equals(SettlementEnum.END_SETTLEMENT_REQUEST.getMessage()))
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.ALREADY_USED.getCode(),"Completion Code has already been used",settlementRequest));

              if(!loadCompletionCodeManager.getAdminStatus().equals(AdminSettlementStatusEnum.STARTED.getMessage()))
                  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"You cannot request completion when a job has not started",settlementRequest));

              loadCompletionCodeManager.setStatus(SettlementEnum.END_SETTLEMENT_REQUEST.getMessage());
              loadCompletionCodeManager.setAdminStatus(AdminSettlementStatusEnum.COMPLETED.getMessage());
              loadCompletionCodeManagerRepo.save(loadCompletionCodeManager);

              Optional<TruckInfo> truckInfo = truckInfoRepo.findById(loadCompletionCodeManager.getTruckId());
              truckInfo.get().setTruckStatus(TruckEnum.AVAILABLE.getCode());
              truckInfoRepo.save(truckInfo.get());

              //Send Email to admin to close out job
              mailService.sendSettlementEmailToAdmin(env.getProperty("username"),userAccount.get(),SettlementTypeEnum.COMPLETION_CODE.getMessage(),loadCompletionCodeManager.getCompletionCode());

              String msg = String.format("%s (%s) just successfully used his COMPLETION CODE to be paid %s for &s",userAccount.get().getCompanyName(),
                      userAccount.get().getUsername(),assignJobInfo.getOfferAmount(),jobInfo.get().getJobReferenceNumber());
              smsService.sendAdminPushNotification(msg,VariableUtil.COMPLETION_CODE_SLACK_URL);

              return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),"Settlement request was successful",settlementRequest));
          }
        }catch(Exception e){
          e.printStackTrace();
       }
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(),"An error occurred while trying to process your request",settlementRequest));
    }



    public ResponseEntity settleRequest(int settlementType, String code, boolean isApproved) {
        if(settlementType == SettlementTypeEnum.LOAD_CODE.getCode()){
            LoadCompletionCodeManager loadCompletionCodeManager = loadCompletionCodeManagerRepo.findByLoadCode(code);
            if(loadCompletionCodeManager == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid loadCode value sent", code));

            if(isApproved){
                loadCompletionCodeManager.setAdminStatus(AdminSettlementStatusEnum.STARTED.getMessage());
                loadCompletionCodeManagerRepo.save(loadCompletionCodeManager);
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Load Code has been settled successfully", code));
            }
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "No action was taken since isApproved is false", code));
        }
        else if(settlementType == SettlementTypeEnum.COMPLETION_CODE.getCode()){
            LoadCompletionCodeManager loadCompletionCodeManager = loadCompletionCodeManagerRepo.findByCompletionCode(code);
            if(loadCompletionCodeManager == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid completionCode value sent", code));

            if(isApproved) {
                loadCompletionCodeManager.setAdminStatus(AdminSettlementStatusEnum.COMPLETED.getMessage());
                loadCompletionCodeManagerRepo.save(loadCompletionCodeManager);

                Optional<TruckInfo> truckInfo = truckInfoRepo.findById(loadCompletionCodeManager.getTruckId());
                truckInfo.get().setTruckStatus(TruckEnum.AVAILABLE.getCode());
                truckInfoRepo.save(truckInfo.get());

                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Completion Code has been settled successfully", code));
            }
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "No action was taken since isApproved is false", code));
        }
      return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Settlement type can either be 1(LoadCode) or 2(CompletionCode)!", settlementType));
    }


    public ResponseEntity updateUserInfoWithVerificationDetails(VerificationDetailsRequest verificationDetailsRequest) {
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", verificationDetailsRequest));
    }
}
