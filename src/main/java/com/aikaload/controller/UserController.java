package com.aikaload.controller;

import com.aikaload.asyncservice.AuditTrailService;
import com.aikaload.dto.*;
import com.aikaload.entity.AuditTrail;
import com.aikaload.service.ReviewService;
import com.aikaload.service.SlackService;
import com.aikaload.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

/**
 * <p>
 * This class contains methods that processes user request from the caller
 * @author Favour Ebo
 * created on 2020/07/25
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final AuditTrailService auditTrailService;
    private final SlackService slackService;


    /**
     * This method helps to manage user login
     * @param authenticationRequest contains authentication information
     * @return ResponseEntity contains response information
     */
    @PostMapping(value = "/login")
    public ResponseEntity authenticateUser(@RequestBody AuthenticationRequest authenticationRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"LOGIN",authenticationRequest.getUsername(),new Date()));
        return userService.authenticateUser(authenticationRequest);
    }


    /**
     * This method registers user
     * @param userRegistrationRequest contains user registration information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/register-user")
    public ResponseEntity registerUser(@RequestBody UserRegistrationRequest userRegistrationRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"REGISTER_USER",userRegistrationRequest.toString(),new Date()));
        return userService.registerUser(userRegistrationRequest);
    }


    /**
     * This method retrieves user information by Id
     * @param id userId
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-user-by-id/{id}")
    public ResponseEntity getUserById(@PathVariable long id){
        return userService.getUserById(id);
    }


    /**
     * This method retrieves user information by useername
     * @param username username
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-user-by-username/{username}")
    public ResponseEntity getUserByEmail(@PathVariable String username){
        return userService.getUserByUsername(username);
    }


    /**
     * This method handles reviews
     * @param reviewRequest contains review information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/write-review")
    public ResponseEntity  writeReview(@RequestBody ReviewRequest reviewRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"WRITE_REVIEW",reviewRequest.toString(),new Date()));
        return reviewService.writeReviews(reviewRequest);
    }


    /**
     * This method handles reviews
     * @param reviewId an ID that uniquely identifies a review
     * @return  ResponseEntity contains response
     */
    @GetMapping("/remove-review-by-id/{reviewId}")
    public ResponseEntity removeReviewById(@PathVariable Long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    /**
     * This method handles reviews
     * @param userId an ID that uniquely identifies a user
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-review-and-aggregate-ratings-by-userId/{userId}")
    public ResponseEntity  getReviewAndAggregateRatingsByUserId(@PathVariable Long userId) {
        return reviewService.getReviewsAndAggregateRatingByUserId(userId);
    }


    /**
     * This method retrieves users
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-users")
    public ResponseEntity getUser(){
        return userService.getUsers();
    }


    /**
     * This method helps edit user profile
     * @return  ResponseEntity contains response
     */
    @PostMapping("/edit-user-profile")
    public ResponseEntity editProfile(@RequestBody EditProfileRequest editProfileRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_PROFILE",editProfileRequest.toString(),new Date()));
        return userService.editProfile(editProfileRequest);
    }


    /**
     * This method helps to report user
     * @return  ResponseEntity contains response
     */
    @PostMapping("/report-user")
    public ResponseEntity reportUser(@RequestBody ReportUserRequest reportUserRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"REPORT_USER",reportUserRequest.toString(),new Date()));
        return userService.reportUser(reportUserRequest);
    }


    //TODO :::Make this method efficient
    /**
     * This method helps to return all complaints
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-complaints")
    public ResponseEntity getAllComplaints(){
        return userService.getAllComplaints();
    }

    @GetMapping("/get-number-of-times-user-is-reported-by-email/{email}")
    public ResponseEntity getNumberOfTimesUserIsReportedByEmail(@PathVariable String email){
        return userService.getNumberOfTimesUserIsReportedByEmail(email);
    }

    /**
     * This method helps get messages by userId
     * @param userId uniquely identifies a user
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-messages/{userId}")
    public ResponseEntity getMessages(@PathVariable Long userId){
        return userService.getMessages(userId);
    }



    /**
     * This method helps create password reset token
     * @return  ResponseEntity contains response
     */
    @PostMapping("/create-password-reset-token")
    public ResponseEntity createPasswordResetToken(@RequestBody String username, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"CREATE_PASSWORD_RESET_TOKEN",username,new Date()));
        return userService.createPasswordResetToken(username);
    }

    /**
     * This method helps validate password reset token
     * @return  ResponseEntity contains response
     */
    @PostMapping("/validate-password-reset-token")
    public ResponseEntity tokenValidation(@RequestBody String encTokenRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"VALIDATE_PASSWORD_RESET_TOKEN",encTokenRequest,new Date()));
        return userService.tokenValidation(encTokenRequest);
    }


    /**
     * This method helps change password
     * @return  ResponseEntity contains response
     */
    @PostMapping("/change-password")
    public ResponseEntity changePassword(@RequestBody PasswordTokenRequest passwordTokenRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"CHANGE_PASSWORD",String.valueOf(passwordTokenRequest.getUserId()),new Date()));
        return userService.updatePassword(passwordTokenRequest);
    }


    /**
     * This method helps to send message to admin slack channel
     * @param message
     * @return
     */
    @PostMapping("/send-slack-message")
    public ResponseEntity sendSlackMessage(@RequestBody String message){
        log.info("Slack Message:{}",message);
        return slackService.sendMessageToAdmin(message);
    }

}
