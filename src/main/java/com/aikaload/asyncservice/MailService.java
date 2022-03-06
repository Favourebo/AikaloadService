package com.aikaload.asyncservice;

import com.aikaload.entity.*;
import com.aikaload.enums.MessageTypeEnum;
import com.aikaload.repo.MessageLogRepo;
import com.aikaload.repo.TruckInfoRepo;
import com.aikaload.repo.UserAccountRepo;
import com.aikaload.repo.UserRoleRepo;
import com.aikaload.utils.MailUtils;
import com.aikaload.utils.SmsUtils;
import com.aikaload.utils.VariableUtil;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import java.util.*;


@Service
@Log4j2
@AllArgsConstructor
public class MailService {
    private final Environment env;
    private final SpringTemplateEngine templateEngine;
    private final UserRoleRepo userRoleRepo;
    private final UserAccountRepo userAccountRepo;
    private final MessageLogRepo messageLogRepo;
    private final TruckInfoRepo truckInfoRepo;
    private final SmsService smsService;


    public void showInterestMailAction(JobInfo jobInfo,UserAccount userAccount){
        Map<String,Object> mailProps = new HashMap<>();
        mailProps.put(VariableUtil.RECEIVER_NAME,jobInfo.getUserAccount().getCompanyName());
        mailProps.put("tcompanyName",userAccount.getCompanyName());
      //mailProps.put("tusername",userAccount.getUsername());
        mailProps.put("jobSummary",jobInfo.getJobSummary());

        sendEmail(new MailUtils(mailProps, VariableUtil.SHOW_INTEREST_EMAIL, jobInfo.getUserAccount().getUsername(), jobInfo.getUserAccount().getCompanyName(),
                "NEW JOB INTEREST FROM ==>" + userAccount.getCompanyName()),jobInfo.getUserAccount().isEmailNotification());
    }



    @Async("executorB")
    public void sendEmail(MailUtils mailUtils, boolean userSendMailStatus) {
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;
        client = new MailjetClient(env.getProperty("email.api.key"), env.getProperty("email.api.secret"), new ClientOptions("v3.1"));
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "admin@aikaloads.com")
                                        .put("Name", env.getProperty("name")))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", mailUtils.getTo())
                                                .put("Name", mailUtils.getName())))
                                .put(Emailv31.Message.SUBJECT, mailUtils.getSubject())
                                .put(Emailv31.Message.HTMLPART, getMessage(mailUtils))
                                .put(Emailv31.Message.CUSTOMID, env.getProperty("email.custom.id"))));
        try {
            if(userSendMailStatus){
                response = client.post(request);
                log.info(response.getStatus());
            }
        } catch (Exception e) {
            log.error("<<<<<<An error occurred while trying to send mail");
        }
    }



    @Async("executorA")
    public void sendTruckOwnersEmail(JobInfo jobInfo){
         try{
            //Get UserRole
            UserRole userRole = userRoleRepo.findByUserRoleName(VariableUtil.TRUCK_OWNER);

            //Get UserAccount
            List<UserAccount> truckOwners = userAccountRepo.findByUserRole(userRole);

            for(UserAccount truckOwner:truckOwners){
                boolean sendMail = false;

                 List<TruckInfo> truckInfoList = truckInfoRepo.findByUserAccount(truckOwner);


                 //Checking if user matches truck type specified in job request
                 for(TruckInfo truckInfo: truckInfoList){
                    if(truckInfo.getTruckType().getId() == jobInfo.getTruckType().getId())
                       sendMail = true;
                     }

                 if(sendMail) {
                     sendMail = false;
                    log.info("Sending Created Job Email To ::{}",truckOwner.getCompanyName());
                   //Send Email
                   MailUtils mail = new MailUtils();
                   mail.setTo(truckOwner.getUsername());
                   mail.setSubject("New Job Request");
                   mail.setName(truckOwner.getCompanyName());
                   mail.setHtmlFileName(VariableUtil.CREATE_JOB_HTML_FILE);

                   Map<String, Object> mailProps = new HashMap<>();
                   mailProps.put(VariableUtil.RECEIVER_NAME, truckOwner.getCompanyName());
                   mailProps.put(VariableUtil.LOAD_OWNER_NAME, jobInfo.getUserAccount().getCompanyName());
                   mailProps.put(VariableUtil.QUANTITY, jobInfo.getTruckNo());
                   mailProps.put(VariableUtil.TRUCK_TYPE, jobInfo.getTruckType().getName());
                   mailProps.put(VariableUtil.MATERIAL, jobInfo.getLoadCategory().getName());
                   mailProps.put(VariableUtil.PICK_UP, jobInfo.getPickUpAddressCity() + "," + jobInfo.getPickUpAddressState());
                   mailProps.put(VariableUtil.DROP_OFF, jobInfo.getDeliveryAddressCity() + "," + jobInfo.getDeliveryAddressState());
                   mailProps.put(VariableUtil.INTEREST_LINK, String.format("%s?singjid=%s", env.getProperty("email.showinterest.context.path"),jobInfo.getId()));
                   mail.setMailProps(mailProps);

                   //Log message to the database
                   MessageLog messageLog = new MessageLog();
                   messageLog.setCreatedDate(new Date());
                   messageLog.setLoggerId(jobInfo.getUserAccount().getId());
                   messageLog.setRecipientId(truckOwner.getId());
                   String message = String.format("%s has created a job for %s %s to move %s from %s to %s",
                           jobInfo.getUserAccount().getCompanyName(),
                           jobInfo.getTruckNo(),
                           jobInfo.getTruckType().getName(),
                           jobInfo.getLoadCategory().getName(),
                           jobInfo.getPickUpAddressCity() + "," + jobInfo.getPickUpAddressState(),
                           jobInfo.getDeliveryAddressCity() + "," + jobInfo.getDeliveryAddressState()
                    );
                    messageLog.setMessage(message);
                    messageLog.setMessageType(MessageTypeEnum.NEW_JOB.getCode());
                    messageLogRepo.save(messageLog);

                    sendEmail(mail,truckOwner.isEmailNotification());

                     //Send Sms
                     SmsUtils smsUtils = new SmsUtils("Aikapremium Job Creation Message",String.format(VariableUtil.CREATE_JOB_MESSAGE,truckOwner.getCompanyName(),
                             jobInfo.getUserAccount().getCompanyName()),truckOwner.getMobilePhone(),"234");
                     smsService.sendSms(smsUtils,truckOwner.getId(),truckOwner.isSmsNotification(),truckOwner.isWhatsappNotification(),truckOwner.getWalletBalance());
                 }
            }
        }catch(Exception e){
              log.error("An error occurred while trying to send truck owners an email:::error::{}",e);
        }
    }


    @Async("executorC")
    public void sendReportEmailToAdmin(String adminEmail,UserAccount reporter,UserAccount userReported,String comment){
         try{
            //for(UserAccount admin: admins){
                //Send Email
                MailUtils mail = new MailUtils();
                mail.setTo(adminEmail);
                mail.setSubject("New Complaint");
                mail.setName("Admin");
                mail.setHtmlFileName(VariableUtil.REPORT_USER_HTML_FILE);

                Map<String, Object> mailProps = new HashMap<>();
                mailProps.put(VariableUtil.RECEIVER_NAME, "Admin");
                mailProps.put("rcompanyName", reporter.getCompanyName());
                mailProps.put("rusername", reporter.getUsername());
                mailProps.put("complaint", comment);
                mailProps.put("ccompanyName", userReported.getCompanyName());
                mailProps.put("cusername", userReported.getUsername());
                mail.setMailProps(mailProps);
                sendEmail(mail,true);
        }catch(Exception e){
            log.error("An error occurred while trying to send administrators an email:::error::{}",e);
        }
    }

    public void sendSettlementEmailToAdmin(String adminEmail, UserAccount userAccount, String settlementType, String loadCode) {
        try{

            //for(UserAccount admin: admins){
            //Send Email
            MailUtils mail = new MailUtils();
            mail.setTo(adminEmail);
            mail.setSubject("New Load Code Settlement Request");
            mail.setName("Admin");
            mail.setHtmlFileName(VariableUtil.ADMIN_SETTLEMENT_EMAIL_HTML);

            Map<String, Object> mailProps = new HashMap<>();
            mailProps.put(VariableUtil.RECEIVER_NAME, "Admin");
            mailProps.put("rcompanyName", userAccount.getCompanyName());
            mailProps.put("rusername", userAccount.getUsername());
            mailProps.put("settlementtype", settlementType);
            mailProps.put("loadCode", loadCode);
            mail.setMailProps(mailProps);
            sendEmail(mail,true);
        }catch(Exception e){
            log.error("An error occurred while trying to send administrators an email:::error::{}",e);
        }
    }




    private String getMessage(MailUtils mailutils){
        try {
            Context context = new Context();
            mailutils.getMailProps().put(VariableUtil.IMAGE_URL,env.getProperty("email.welcome.image.url"));
            context.setVariables(mailutils.getMailProps());
            return templateEngine.process(mailutils.getHtmlFileName(), context);
        }catch(Exception e){
            log.error("An error occurred while trying to receive message::"+e.getMessage());
        }
        return null;
    }


}
