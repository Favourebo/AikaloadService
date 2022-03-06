package com.aikaload.controller;

import com.aikaload.asyncservice.AuditTrailService;
import com.aikaload.dto.*;
import com.aikaload.entity.AuditTrail;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.service.AccountService;
import com.aikaload.service.FeeConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Date;


@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountController {

    private final AccountService accountService;
    private final FeeConfigurationService feeConfigurationService;
    private final AuditTrailService auditTrailService;

    /**
     * This method helps fund the user wallet
     *
     * @param walletRequest contains wallet information
     * @return ResponseEntity contains response
     */
    @PostMapping("/fund-wallet")
    public ResponseEntity fundWallet(@RequestBody WalletRequest walletRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"FUND_WALLET",walletRequest.toString(),new Date()));
        return accountService.fundWallet(walletRequest);
    }

    /**
     * This method helps settles request
     *
     * @param settlementRequest contains settlement request
     * @return ResponseEntity contains response
     */
    @PostMapping("/settlement-request")
    public ResponseEntity settlementRequest(@RequestBody SettlementRequest settlementRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"SETTLEMENT_REQUEST",settlementRequest.toString(),new Date()));
        return accountService.settlementRequest(settlementRequest);
    }


    /**
     * This method helps debit the user wallet
     *
     * @param walletRequest contains wallet information
     * @return ResponseEntity contains response
     */
    @PostMapping("/debit-wallet")
    public ResponseEntity debit(@RequestBody WalletRequest walletRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"DEBIT_WALLET",walletRequest.toString(),new Date()));
        return accountService.debitWallet(walletRequest);
    }


    /**
     * This method helps edit setting
     *
     * @param editSettingRequest contains setting information
     * @return ResponseEntity contains response
     */
    @PostMapping("/edit-setting")
    public ResponseEntity editSetting(@RequestBody EditSettingRequest editSettingRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_SETTING",editSettingRequest.toString(),new Date()));
        return accountService.editSetting(editSettingRequest);
    }


    /**
     * This method returns verification cost
     *
     * @return ResponseEntity contains response
     */
    @PostMapping("/get-verification-cost")
    public ResponseEntity getVerificationCost(Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"GET_VERIFICATION_COST","",new Date()));
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),feeConfigurationService.getVerificationCost()));
    }

    /**
     * This method edits verification cost
     *
     * @return ResponseEntity contains response
     */
    @PostMapping("/edit-verificaction-cost/{newVerificationCost}")
    public ResponseEntity editVerificationCost(@PathVariable BigDecimal newVerificationCost, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_VERIFICATION_COST",newVerificationCost.toString(),new Date()));
        return feeConfigurationService.editVerificationCost(newVerificationCost);
    }

    /**
     * This method sends verification message
     *
     * @return ResponseEntity contains response
     */
    @GetMapping("/send-verification-message/{userId}")
    public ResponseEntity sendVerificationMessage(@PathVariable Long userId, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"SEND_VERIFICATION_MESSAGE",String.valueOf(userId),new Date()));
        return accountService.sendVerificationMessage(userId);
    }


    /**
     * This method get user verification status
     *
     * @return ResponseEntity contains response
     */
    @GetMapping("/get-user_verification-status/{userId}")
    public ResponseEntity getUserVerificationStatus(@PathVariable Long userId, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"GET_USER_VERIFICATION_STATUS",String.valueOf(userId),new Date()));
        return accountService.getUserVerificationStatus(userId);
    }


    /**
     * This method helps edit user verification status
     * @return  ResponseEntity contains response
     */
    @GetMapping("/edit-user-verification-status/{userId}/{verificationStatus}")
    public ResponseEntity editUserVerificationStatus(@PathVariable Long userId,@PathVariable boolean verificationStatus, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_USER_VERIFICATION_STATUS",String.format("%s/%s",userId,verificationStatus),new Date()));
        return accountService.editUserVerificationStatus(userId,verificationStatus);
    }


    /**
     * This method helps complete settlement request
     * @return  ResponseEntity contains response
     */
    @GetMapping("/admin-settle-request/{settlementType}/{code}/{isApproved}")
    public ResponseEntity editUserVerificationStatus(@PathVariable int settlementType,@PathVariable String code,@PathVariable boolean isApproved, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"ADMIN_SETTLE_REQUEST",String.format("%s/%s/%s",settlementType,code,isApproved),new Date()));
        return accountService.settleRequest(settlementType,code,isApproved);
    }


    /**
     * This method helps update user info with verification details
     * @return  ResponseEntity contains response
     */
    @PostMapping("update-userinfo-with-verification-details")
    public ResponseEntity updateUserInfoWithVerificationDetails(@RequestBody VerificationDetailsRequest verificationDetailsRequest){
          return accountService.updateUserInfoWithVerificationDetails(verificationDetailsRequest);
    }
}
