package com.aikaload.asyncservice;

import com.aikaload.dto.Response;
import com.aikaload.dto.WalletRequest;
import com.aikaload.entity.TransactionHistory;
import com.aikaload.entity.UserAccount;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.enums.TransactionTypeEnum;
import com.aikaload.host.RestConnector;
import com.aikaload.repo.TransactionHistoryRepo;
import com.aikaload.repo.UserAccountRepo;
import com.aikaload.service.FeeConfigurationService;
import com.aikaload.utils.SmsUtils;
import com.aikaload.utils.ValidationUtil;
import com.aikaload.utils.VariableUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class SmsService {
    private final RestConnector restConnector;
    private final UserAccountRepo userAccountRepo;
    private final FeeConfigurationService feeConfigurationService;
    private final PushNotificationService pushNotificationService;
    private final TransactionHistoryRepo transactionHistoryRepo;

    @Value("${ui.base.path}")
    private String uiBaseUrl;

    @Value("${token}")
    private String token;

    @Value("${track.send.url}")
    private String trackSendUrl;

    @Value("${sender.name}")
    private String senderName;


    @Async("executorB")
    public void sendSms(SmsUtils smsUtils, Long userId, boolean userSendSmsStatus,boolean userSendWhatsappStatus, BigDecimal walletBalance) {

        //In App Notification
        String channel = String.format("%s%s", VariableUtil.CHANNEL, userId);
        pushNotificationService.publishMessage(smsUtils.getMessage(), VariableUtil.EVENT, channel);

        //Send admin push notification



        if(userSendWhatsappStatus == true){
            //Send Sms Message
            JSONObject request = new JSONObject();
            request.put("token", token);
            request.put("sender", senderName);
            request.put("name", smsUtils.getName());
            request.put("message", String.format("%s [%s]",smsUtils.getMessage(),uiBaseUrl));
            request.put("contacts", smsUtils.getContacts());
            request.put("type", new String[]{"whatsapp"});
            ResponseEntity<String> smsRsp = restConnector.sendPostRequest(trackSendUrl, request.toString(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
        }

        if(userSendSmsStatus == true){
            //Do SMS Debit
            BigDecimal smsCost = feeConfigurationService.getSmsCost();

            //Check if customer has money in wallet
            if(walletBalance.doubleValue() > smsCost.doubleValue()) {
                //Send Sms Message
                JSONObject request = new JSONObject();
                request.put("token", token);
                request.put("sender", senderName);
                request.put("url", uiBaseUrl);
                request.put("name", smsUtils.getName());
                request.put("message", String.format("%s [url]",smsUtils.getMessage()));
                request.put("contacts", smsUtils.getContacts());
                request.put("type", new String[]{"sms"});
                ResponseEntity<String> smsRsp = restConnector.sendPostRequest(trackSendUrl, request.toString(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

                if (smsRsp.getStatusCode().equals(HttpStatus.OK)) {
                    //Debit Customer Transaction Value
                    debitWallet(new WalletRequest(smsCost, String.format("SMS-ALERT-%s", smsUtils.getContacts()), userId));
                }
            }
            log.error("Insufficient wallet balance for {},application was unable to send SMS",userId);
        }
    }

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
                transactionHistoryRepo.save(populateTransactionHistory(walletRequest, TransactionTypeEnum.DEBIT.getMessage()));
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Wallet was debited successfully", walletRequest));
            }
        } catch (Exception e) {
            log.error("<<<<An error occurred while trying to fund wallet:::error::{}", e);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), walletRequest));
    }


    /**
     * This method helps populate transactionHistory
     *
     * @param walletRequest contains
     * @return
     */
    private static TransactionHistory populateTransactionHistory(WalletRequest walletRequest, String transactionType) {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setAmount(walletRequest.getTransactionAmount());
        transactionHistory.setNarration(String.format("%s%s%s", transactionType, VariableUtil.DEFAULT_NARRATION, walletRequest.getTransactionReference()));
        transactionHistory.setTransactionDate(new Date());
        transactionHistory.setTransactionRef(walletRequest.getTransactionReference());
        transactionHistory.setTransactionType(transactionType);
        transactionHistory.setUserId(walletRequest.getUserId());
        return transactionHistory;
    }


    public void sendAdminPushNotification(String message, String slackUrl){
        //Send Sms Message
        JSONObject request = new JSONObject();
        request.put("text", message);
        restConnector.sendPostRequest(slackUrl, request.toString(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
    }

}
