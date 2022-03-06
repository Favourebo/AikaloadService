package com.aikaload.service;

import com.aikaload.asyncservice.MailService;
import com.aikaload.asyncservice.SmsService;
import com.aikaload.dto.*;
import com.aikaload.entity.*;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.repo.*;
import com.aikaload.utils.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
import com.aikaload.enums.UserEnum;


@Service
@AllArgsConstructor
@Log4j2
public class UserService{
    private final UserAccountRepo userAccountRepo;
    private final UserRoleRepo userRoleRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MailService mailService;
    private final PasswordResetRepo passwordResetRepo;
    private final Environment env;
    private final MessageLogRepo messageLogRepo;
    private final ComplaintsRepo complaintsRepo;
    private final SmsService smsService;

    /**
     * This authenticates the user using the username and password
     * @param authenticationRequest contains authentication information
     * @return ResponseEntity contains response
     */
    public ResponseEntity<Object> authenticateUser(AuthenticationRequest authenticationRequest) {
        try {
            //Validate incoming request
            log.info(">>>>>>Validating if authenticationRequest is null");
            if (authenticationRequest == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "auth request cannot be null", null));
            }

            //Validating username
            log.info(">>>>>>>Validating username::" + authenticationRequest.getUsername());
            boolean usernameValidation = ValidationUtil.validateEmailAddress(authenticationRequest.getUsername());
            if (!usernameValidation) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "invalid username", null));

            }

            //Find record by username in the database
            log.info(">>>>Find user information by username in the database::" + authenticationRequest.getUsername());
            UserAccount userAccount = userAccountRepo.findByUsername(authenticationRequest.getUsername());

            if(userAccount == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "no record found for username", null));
            }

            log.info(">>>>>Validating password received");
            if (!bCryptPasswordEncoder.matches(authenticationRequest.getPassword(),userAccount.getPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "invalid password", null));
            }

            //Notify slack
            String slackMessage=String.format("%s from %s just logged-in to aikapremium as a %s",userAccount.getUsername(),userAccount.getCountry(),userAccount.getUserRole().getUserRoleName());
            smsService.sendAdminPushNotification(slackMessage,VariableUtil.VALIDATION_SLACK_URL);

            return ResponseEntity.status(HttpStatus.OK).body(new Response(ResponseEnum.OK.getCode(), "User Authentication was successful", returnUserAccountInMapFormat(userAccount)));
        }catch(Exception e){
            log.error("<<<<<<An exception occurred during user authentication::"+e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), "An error occurred while trying to authenticate user", null));
        }
    }



     public ResponseEntity getMessages(Long userId) {
        try {
            List<MessageLog> messageLogs = messageLogRepo.findByRecipientId(userId);

            if (messageLogs.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), "No record was found for userId specified", userId));

            List<Object> results = new ArrayList();
            for (MessageLog messageLog : messageLogs) {
                Map<String, Object> result = new HashMap<>();
                Optional<UserAccount> user = userAccountRepo.findById(messageLog.getLoggerId());
                result.put("loggedBy", returnUserAccountInMapFormat(user.get()));
                result.put("createdDate", messageLog.getCreatedDate());
                result.put("message", messageLog.getMessage());
                result.put("messageType", messageLog.getMessageType());
                results.add(result);
            }
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", results));

        }catch(Exception e){
            log.error("<<<<<<An exception occurred during user authentication::{}",e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), "An error occurred while trying to fetch records", userId));

        }
    }



    /**
     * This method helps to register user
     * @param userRegistrationRequest contains userRegistrationRequest information
     * @return ResponseEntity contains response
     */
    public ResponseEntity<Object> registerUser(UserRegistrationRequest userRegistrationRequest) {
        try {

            log.info("\n\n\n");
            log.info(">>>>>>>New user registration request from calling client::::"+userRegistrationRequest.toString());

            //Validate Incoming User request
            Map<String, String> validationResponse = validateUserRequest(userRegistrationRequest);
            if(!validationResponse.get(VariableUtil.CODE).equals(ResponseEnum.OK.getCode())){
                //Send response back to calling client
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(validationResponse.get(VariableUtil.CODE), validationResponse.get(VariableUtil.MESSAGE), userRegistrationRequest));
            }

            log.info(">>>>>>>Populate User Account:::");
            UserAccount userAccount = populateUserAccount(userRegistrationRequest);

            //create user role object
            log.info(">>>>>>>Trying to get user role information using the user role id:::"+userRegistrationRequest.getUserRoleId());
            Optional<UserRole> userRole = userRoleRepo.findById(userRegistrationRequest.getUserRoleId());
            if (!userRole.isPresent()) {
                //Return record not found for userRoleId
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userRoleId", userRegistrationRequest));
            }
            userAccount.setUserRole(userRole.get());

            //Persist user in the database
            UserAccount createAccountRsp = userAccountRepo.save(userAccount);

            //If user wasn't registered, terminate request and alert user
            if (createAccountRsp == null) {
                //Could not create user account
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.USER_REGISTRATION_FAILED.getCode(), ResponseEnum.USER_REGISTRATION_FAILED.getMessage(), userRegistrationRequest));
            }

            //Build Mail Props
            MailUtils mail = new MailUtils();
            mail.setTo(userAccount.getUsername());
            mail.setSubject("User Registration on Aikaload");
            mail.setName(userAccount.getCompanyName());
            mail.setHtmlFileName(VariableUtil.REGISTRATION_HTML_FILE);

            Map<String,Object> mailProps = new HashMap<>();
            mailProps.put(VariableUtil.RECEIVER_NAME,createAccountRsp.getCompanyName());
            mailProps.put(VariableUtil.USERNAME,createAccountRsp.getUsername());
            mail.setMailProps(mailProps);

            //send user a welcome email
            mailService.sendEmail(mail,true);


            //Notify slack
            String slackMessage=String.format("%s from %s just registered as a %s",createAccountRsp.getUsername(),createAccountRsp.getCountry(),createAccountRsp.getUserRole().getUserRoleName());
            smsService.sendAdminPushNotification(slackMessage,VariableUtil.REGISTRATION_SLACK_URL);

            return ResponseEntity.status(HttpStatus.OK).body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), returnUserAccountInMapFormat(createAccountRsp)));
          }catch (Exception e) {
            log.error("<<<<<An error occurred while trying to register user::"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), null));
        }



    }

    /**
     * This method with the of the userId retrieves user Information from the database
     * @param userId userId
     * @return ResponseEntity contains response
     */
    public ResponseEntity<Object> getUserById(long userId) {
        Optional<UserAccount> optionalUserAccount = userAccountRepo.findById(userId);
        if(optionalUserAccount.isPresent()){
            Map<String, Object> userAccount = returnUserAccountInMapFormat(optionalUserAccount.get());
            return ResponseEntity.status(HttpStatus.OK).body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(), userAccount));
        }
        return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"UserId is Invalid", null));
    }

    /**
     * This method with the of the userId retrieves user Information from the database
     * @param username username
     * @return ResponseEntity contains response
     */
    public ResponseEntity<Object> getUserByUsername(String username) {
        UserAccount xuserAccount = userAccountRepo.findByUsername(username);
        if(xuserAccount != null){
            Map<String, Object> userAccount = returnUserAccountInMapFormat(xuserAccount);
            return ResponseEntity.status(HttpStatus.OK).body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(), userAccount));
        }
        return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"Username is Invalid", null));
    }



    /**
     * This method retrieves users
     * @return ResponseEntity contains response
     */
    public ResponseEntity<Object> getUsers() {

        List<UserAccount> userAccounts = userAccountRepo.findAll();
        if (!userAccounts.isEmpty()) {
            List<Object> result = new ArrayList<>();
            for (UserAccount u : userAccounts) {
                Map<String, Object> userAccount = returnUserAccountInMapFormat(u);
                result.add(userAccount);
            }
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), result));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(),"No User was found", null));
    }


    /**
     * Validates password token generated
     * @param encTokenRequest generated authentication value
     * @return
     */
    public ResponseEntity<Object> tokenValidation(String encTokenRequest) {
        // validate password reset token
        try {
            //Decrypt request
            String decTokenRequest = EncryptUtil.decrypt(encTokenRequest,VariableUtil.SECRET);
            if(decTokenRequest== null){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Server was unable to interpret encrypted request sent", null));

            }
            String[] part = decTokenRequest.split("&");
            String token = part[1];
            Long id = Long.valueOf(part[0]);

            PasswordResetToken passwordResetToken = passwordResetRepo.findByToken(token);

            if(passwordResetToken == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid password reset token sent", null));
            }

            if(!(passwordResetToken.getUserAccount().getId().equals(id))) {
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), " User Id retrieved does not match user value sent", null));
            }

            Optional<UserAccount> userAccount = userAccountRepo.findById(id);
            if(userAccount.isPresent()){
                return  ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Token validation was successful", returnUserAccountInMapFormat(userAccount.get())));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return  ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(),null));
    }



    /**
     * Update Password
     * @param request
     * @return
     */
    public ResponseEntity<Object> updatePassword(PasswordTokenRequest request) {
        try{
            if(request.getUserId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId value sent", null));
            }
            Optional<UserAccount> userAccount = userAccountRepo.findById(request.getUserId());
            if(!userAccount.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId value sent", null));
            }

            if(request.getPassword() == null || request.getPassword().length() < 5){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Password cannot be less than 5 in length", null));
            }
            userAccount.get().setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
            userAccountRepo.save(userAccount.get());
            return  ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Your password has been successfully updated", returnUserAccountInMapFormat(userAccount.get())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(),null));

    }

    /**
     * This methods helps to report a user
     * @param reportUserRequest contains user to be reported
     * @return ResponseEntity contains response information
     */
    public ResponseEntity reportUser(ReportUserRequest reportUserRequest) {
       try {
           //Validate incoming request
           String validationResponse = ValidationUtil.validateReportUser(reportUserRequest);
           if (validationResponse != null)
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), validationResponse, reportUserRequest));

           Complaints complaints = new Complaints();
           complaints.setReportedBy(reportUserRequest.getReporterId());
           complaints.setUserReported(reportUserRequest.getUserReportedId());
           complaints.setMessage(reportUserRequest.getComment());
           complaints.setTreated(false);
           complaints.setCreatedDate(new Date());

           Complaints saveRsp = complaintsRepo.save(complaints);
           if(saveRsp != null) {

                //Send mail to all administrators
               //List<UserAccount> admins =  userAccountRepo.findByUserRoleName(VariableUtil.ADMIN);
               Optional<UserAccount> reportedBy  = userAccountRepo.findById(reportUserRequest.getReporterId());
               Optional<UserAccount> userReported  = userAccountRepo.findById(reportUserRequest.getUserReportedId());
               mailService.sendReportEmailToAdmin(env.getProperty("username"),reportedBy.get(),userReported.get(),reportUserRequest.getComment());

               String msg = String.format("%s says the %s  (%s)",reportedBy.get().getCompanyName(),userReported.get().getCompanyName(),complaints.getMessage());
               smsService.sendAdminPushNotification(msg,VariableUtil.REPORT_SLACK_URL);

               return  ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Your report has been submitted successfully",reportUserRequest));
           }
       }catch(Exception e){
           log.error("<<<<<<< An error occurred while trying to report user::::errorMessage::{}",e);
       }
       return  ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(),reportUserRequest));
    }


    /**
     * This methods helps to get all complaints
     * @return ResponseEntity contains response information
     */
    public ResponseEntity getAllComplaints() {
         try{
            List<Complaints> complaints = complaintsRepo.findAll();
            if(complaints.isEmpty())
                return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), "No record found",null));

            List<Object> results = new ArrayList<>();
            for(Complaints complaint: complaints){
                Map<String,Object> result = new HashMap<>();
                Optional<UserAccount> reportedBy = userAccountRepo.findById(complaint.getReportedBy());
                Optional<UserAccount> userReported = userAccountRepo.findById(complaint.getUserReported());
                result.put("reportedBy",returnUserAccountInMapFormat(reportedBy.get()));
                result.put("userReported",returnUserAccountInMapFormat(userReported.get()));
                result.put("createdDate",complaint.getCreatedDate());
                result.put("comment",complaint.getMessage());
                result.put("treated",complaint.isTreated());
                results.add(result);
            }

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),"Successful",results));

        }catch(Exception e){
            log.error("<<<<<<< An error occurred while trying to report user::::errorMessage::{}",e);
        }
        return  ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(),null));
    }





    /**
     * This method updates user profile
     * @return ResponseEntity contains response
     */
    public ResponseEntity editProfile(EditProfileRequest editProfileRequest) {
        String rspMessage = null;
        // Get the file and save it somewhere
        try {
            //Get userDetails from the database for update
            Optional<UserAccount> updateUserAccount = userAccountRepo.findById(editProfileRequest.getUserId());

            //If userId is not found
            if(!updateUserAccount.isPresent()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"Invalid userId!",editProfileRequest));
            }

            if(editProfileRequest.getFirstName() != null){
                updateUserAccount.get().setFirstName(editProfileRequest.getFirstName());
            }

            if(editProfileRequest.getLastName() != null){
                updateUserAccount.get().setLastName(editProfileRequest.getLastName());
            }

            if(editProfileRequest.getPhoneNumber() != null){
                updateUserAccount.get().setMobilePhone(editProfileRequest.getPhoneNumber());
            }

            if(editProfileRequest.getCompanyName() != null){
                updateUserAccount.get().setCompanyName(editProfileRequest.getCompanyName());
            }

            if(editProfileRequest.getRoutes() != null){
                updateUserAccount.get().setRoutes(editProfileRequest.getRoutes());
            }

            if(editProfileRequest.getAddress() != null){
                updateUserAccount.get().setAddressLine1(editProfileRequest.getAddress());
            }

            if(editProfileRequest.getTermsOfService() != null){
                updateUserAccount.get().setTermsOfService(editProfileRequest.getTermsOfService());
            }

            if(editProfileRequest.getBio() != null){
                updateUserAccount.get().setBio(editProfileRequest.getBio());
            }

            if(editProfileRequest.getUserProfileUrl() != null){
                updateUserAccount.get().setUserProfileUrl(editProfileRequest.getUserProfileUrl());
            }

            if(editProfileRequest.getCity() != null){
                updateUserAccount.get().setCity(editProfileRequest.getCity());
            }

            if(editProfileRequest.getState() != null){
                updateUserAccount.get().setState(editProfileRequest.getState());
            }

            if(editProfileRequest.getCountry() != null){
                updateUserAccount.get().setCountry(editProfileRequest.getCountry());
            }

            if(editProfileRequest.getWhatsappNumber() != null){
                updateUserAccount.get().setWhatsappNumber(editProfileRequest.getWhatsappNumber());
            }
            updateUserAccount.get().setLastModifiedDate(new Date());
            UserAccount updatedUserAccountRsp = userAccountRepo.save(updateUserAccount.get());

            if(updatedUserAccountRsp != null){
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),returnUserAccountInMapFormat(updatedUserAccountRsp)));
            }
            else{
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(),ResponseEnum.OPERATION_FAILED.getMessage(),editProfileRequest));
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(),ResponseEnum.AN_ERROR_OCCURRED.getMessage(),editProfileRequest));
        }
    }


    public ResponseEntity<Object> createPasswordResetToken(String username) {
        try {
            UserAccount userAccount = userAccountRepo.findByUsername(username);
            if (userAccount != null) {
                // ----Generate reset password token for user
                String token = CommonUtil.returnToken();
                PasswordResetToken passwordResetToken = new PasswordResetToken(token, userAccount, new Date());
                PasswordResetToken rspObj = passwordResetRepo.save(passwordResetToken);
                if (rspObj == null) {
                    return  ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to create a password reset token", username));
                }
                String subject = "Password Reset Email";
                String resetLink =  String.format("%s%s%s",env.getProperty("email.reset.context.path"),"?id=",EncryptUtil.encrypt(userAccount.getId()
                        + "&" + token, VariableUtil.SECRET));


                //Build Mail Props
                MailUtils mail = new MailUtils();
                mail.setTo(userAccount.getUsername());
                mail.setSubject(subject);
                mail.setName(userAccount.getCompanyName());
                mail.setHtmlFileName(VariableUtil.PASSWORD_RESET_HTML_FILE);
                Map<String,Object> mailProps = new HashMap<>();
                mailProps.put(VariableUtil.RECEIVER_NAME,userAccount.getCompanyName());
                mailProps.put(VariableUtil.RESET_LINK,resetLink);
                mail.setMailProps(mailProps);

                mailService.sendEmail(mail,true);

                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "A password reset link has been sent to" + userAccount.getUsername(), username));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid username", username));
            }
        } catch (Exception e) {
            log.error("<<<<An error occurred during token generation:::" + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), username));
    }


    /**
     * Populate user account object
     * @param userRegistrationRequest contains registration information
     * @return UserAccount contains user account information
     */
    private UserAccount populateUserAccount(UserRegistrationRequest userRegistrationRequest){
        //populate user account object
        UserAccount userAccount = new UserAccount();
        userAccount.setCompanyName(userRegistrationRequest.getCompanyName().toUpperCase());
        userAccount.setMobilePhone(userRegistrationRequest.getMobilePhone());
        userAccount.setFirstName(userRegistrationRequest.getFirstName().toUpperCase());
        userAccount.setLastName(userRegistrationRequest.getLastName().toUpperCase());
        userAccount.setPassword(bCryptPasswordEncoder.encode(userRegistrationRequest.getPassword()));
        userAccount.setUsername(userRegistrationRequest.getUsername().toLowerCase().trim());
        userAccount.setAccountInformationUpdated(false);
        userAccount.setAccountVerified(true);
        userAccount.setUserStatus(UserEnum.ACTIVE.getCode());
        userAccount.setDateCreated(new Date());
        return userAccount;
    }




    /**
     * This method helps to validate userRegistrationRequest
     * @param userRegistrationRequest contains information to be validated
     * @return Map<String,String> contains response
     */
    private Map<String,String> validateUserRequest(UserRegistrationRequest userRegistrationRequest){
        Map<String, String> validationRsp = new HashMap<>();
        validationRsp.put(VariableUtil.CODE,ResponseEnum.OK.getCode());


        //Validating if username already exist
        log.info(">>>>>>Validating username:::"+userRegistrationRequest.getUsername());

        boolean emailValidationRsp = ValidationUtil.validateEmailAddress(userRegistrationRequest.getUsername());
        if(!emailValidationRsp){
            validationRsp.put(VariableUtil.CODE,ResponseEnum.INVALID_VALUE.getCode());
            validationRsp.put(VariableUtil.MESSAGE,"Invalid username format");
            return validationRsp;
        }

        UserAccount usernameValidationRsp = userAccountRepo.findByUsername(userRegistrationRequest.getUsername());
        if (usernameValidationRsp != null) {
            validationRsp.put(VariableUtil.CODE,ResponseEnum.VALUE_ALREADY_EXIST.getCode());
            validationRsp.put(VariableUtil.MESSAGE,"Email Address (" + userRegistrationRequest.getUsername() + ") already exist");
            return validationRsp;
        }

        // Validating if mobile phone already exist
        log.info(">>>>>>Validating mobile phone:::"+userRegistrationRequest.getMobilePhone());
        UserAccount mobilePhoneValidationRsp = userAccountRepo.findByMobilePhone(userRegistrationRequest.getMobilePhone());
        if (mobilePhoneValidationRsp != null) {
            validationRsp.put(VariableUtil.CODE,ResponseEnum.VALUE_ALREADY_EXIST.getCode());
            validationRsp.put(VariableUtil.MESSAGE,"Mobile Phone (" + userRegistrationRequest.getMobilePhone() + ") already exist");
            return validationRsp;
        }
        return validationRsp;
    }


    /**
     * This method represents filling the user account object with information
     *
     * @param user
     * @return
     */
    protected static Map<String,Object> returnUserAccountInMapFormat(UserAccount user){
        Map<String, Object> userAccount = new HashMap<>();
        userAccount.put("userId", user.getId());
        userAccount.put("firstName",user.getFirstName());
        userAccount.put("email", user.getUsername());
        userAccount.put("phoneNumber",user.getMobilePhone());
        userAccount.put("lastName",user.getLastName());
        userAccount.put("userRole",user.getUserRole());
        userAccount.put("dateCreated",user.getDateCreated());
        userAccount.put("companyName", user.getCompanyName());
        userAccount.put("routes", user.getRoutes());
        userAccount.put("address",user.getAddressLine1());
        userAccount.put("termsOfService", user.getTermsOfService());
        userAccount.put("bio", user.getBio());
        userAccount.put("userProfileUrl", user.getUserProfileUrl());
        userAccount.put("userStatus",user.getUserStatus());
        userAccount.put("state",user.getState());
        userAccount.put("country",user.getCountry());
        userAccount.put("whatsappNumber",user.getWhatsappNumber());
        userAccount.put("city",user.getCity());
        userAccount.put("isAccountVerified",user.isAccountVerified());
        userAccount.put("walletBalance",user.getWalletBalance());
        userAccount.put("whatsappNotification",user.isWhatsappNotification());
        userAccount.put("smsNotification",user.isSmsNotification());
        userAccount.put("emailNotification",user.isEmailNotification());
        userAccount.put("showPhoneNumber",user.isShowPhoneNumber());
        userAccount.put("showWhatsappNumber",user.isShowWhatsappNumber());
        return userAccount;
    }

    public ResponseEntity getNumberOfTimesUserIsReportedByEmail(String email) {
        try {
            UserAccount userAccount = userAccountRepo.findByUsername(email);
            if (userAccount == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid email sent", email));

            int count = complaintsRepo.countByUserReported(userAccount.getId());
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", count));
        }
        catch(Exception e){
            log.error("An error occurred while trying to get complaint count for email::{}",email);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), "An error occurred while trying to get complaints count for user", email));
        }
    }
}
