package com.aikaload.service;


import com.aikaload.asyncservice.MailService;
import com.aikaload.asyncservice.SmsService;
import com.aikaload.dto.*;
import com.aikaload.entity.*;
import com.aikaload.enums.*;
import com.aikaload.pagination.PaginationResult;
import com.aikaload.pagination.PaginationUtil;
import com.aikaload.pagination.SqlQuery;
import com.aikaload.repo.*;
import com.aikaload.serviceInterface.JobInterface;
import com.aikaload.utils.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@Service("jobService")
@Log4j2
@AllArgsConstructor
public class JobService implements JobInterface{
    private final UserAccountRepo userAccountRepo;
    private final JobInfoRepo jobInfoRepo;
    private final LoadLevelRepo loadLevelRepo;
    private final LoadCategoryRepo loadCategoryRepo;
    private final TruckTypeRepo truckTypeRepo;
    private final TruckInfoRepo truckInfoRepo;
    private final TruckService truckService;
    private final UserService userService;
    private final PaginationUtil paginationUtil;
    private final AssignJobInfoRepo assignJobInfoRepo;
    private final UserRatingLogRepo userRatingLogRepo;
    private final MailService mailService;
    private final VerificationHistoryRepo verificationHistoryRepo;
    private final LoadCompletionCodeManagerRepo loadCompletionCodeManagerRepo;
    private final SmsService smsService;
    private final MessageLogRepo messageLogRepo;
    private final ValidationUtil validationUtil = new ValidationUtil();



    /**
     * This method helps to indicate interest
     *
     * @param showInterestRequest contains request information
     * @return ResponseEntity contains response
     */
    public ResponseEntity showInterest(ShowInterestRequest showInterestRequest) {
        try {
            //Get user's verification status
            VerificationHistory verificationHistory = verificationHistoryRepo.findByUserId(showInterestRequest.getTruckOwnerId());
            Optional<JobInfo> jobInfo = jobInfoRepo.findById(showInterestRequest.getJobId()); //Retrieve JobInformation
            Optional<UserAccount> userAccount = userAccountRepo.findById(showInterestRequest.getTruckOwnerId()); //Retrieve TruckOwner Information
            AssignJobInfo assignJobInfoExistRsp = assignJobInfoRepo.findByAssignedToAndJobInfo(userAccount.get(), jobInfo.get()); //Retrieve Assign Job Info (If it exist)

            //Perform checks on request sent
            ResponseEntity<Object> showInterestChecksRsp = validationUtil.showInterestChecks(showInterestRequest, verificationHistory, userAccount, assignJobInfoExistRsp);
            if (showInterestChecksRsp != null) return showInterestChecksRsp;

            //Create Assign Job Record For Interest
            AssignJobInfo saveRsp = assignJobInfoRepo.save(new AssignJobInfo(null, jobInfo.get(), userAccount.get(), false, false, new Date(), null, new BigDecimal(showInterestRequest.getOfferAmount()),
                    showInterestRequest.getNumOfTrucks(), 0, null, showInterestRequest.getExpectedDeliveryDays(), null));

            if(saveRsp != null){
                //Create Records For Number of Trucks Given By Truck Owner
                for (int truckId : showInterestRequest.getTruckId()) {
                    loadCompletionCodeManagerRepo.save(new LoadCompletionCodeManager(null, saveRsp.getId(), null, null, new Date(), truckId,false,SettlementEnum.FRESH.getMessage(), AdminSettlementStatusEnum.PENDING.getMessage()));
                }

                //Send Mail To Load Owner on interest shown
                mailService.showInterestMailAction(jobInfo.get(), userAccount.get());

                String message = String.format(VariableUtil.JOB_INTEREST,jobInfo.get().getUserAccount().getCompanyName(),
                        userAccount.get().getCompanyName());
                //Send Sms
                SmsUtils smsUtils = new SmsUtils("Aikapremium Show Interest Message",message,jobInfo.get().getUserAccount().getMobilePhone(),"234");
                smsService.sendSms(smsUtils,jobInfo.get().getUserAccount().getId(),jobInfo.get().getUserAccount().isSmsNotification(),jobInfo.get().getUserAccount().isWhatsappNotification(),jobInfo.get().getUserAccount().getWalletBalance());

                //Send Admin Push Notification
                String msg = String.format("%s (%s) just showed interest with %s truck(s) on %s job to move %s with %s %s from %s to %s ====> %s",userAccount.get().getCompanyName(),
                        userAccount.get().getUsername(),showInterestRequest.getNumOfTrucks(),jobInfo.get().getUserAccount().getCompanyName(),
                        jobInfo.get().getLoadCategory().getName(),jobInfo.get().getTruckNo(),jobInfo.get().getTruckType().getName(),
                        jobInfo.get().getPickUpAddress(),jobInfo.get().getDeliveryAddress(),jobInfo.get().getJobReferenceNumber());
                smsService.sendAdminPushNotification(msg,VariableUtil.SHOW_INTEREST_SLACK_URL);

                //Log Message
                logMessage(userAccount.get().getId(), jobInfo.get().getUserAccount().getId(), message, MessageTypeEnum.SHOW_INTEREST.getCode());

                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", showInterestRequest));
            }
        } catch (Exception e) {
            log.error("An error occurred while trying to show interest::{}", e);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), ResponseEnum.OPERATION_FAILED.getMessage(), showInterestRequest));
    }



    /**
     * This methods helps to assign job
     *
     * @param assignJobRequest contains information on job to be assigned
     * @return ResponseEntity
     */
    public ResponseEntity<Object> assignJob(AssignJobRequest assignJobRequest){
        try{
            log.info("\n\n\n");
            log.info(">>>>>New Assign Job Request from calling clients::::"+assignJobRequest.toString());

            log.info(">>>>>Validating Incoming Request");
            String validationRsp = validateAssignJobRequest(assignJobRequest);

            if(!validationRsp.equals(ResponseEnum.OK.getCode()))
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(),validationRsp,assignJobRequest));

            //Get JobInfo from the database
            Optional<JobInfo> jobInfo = jobInfoRepo.findById(assignJobRequest.getJobId());
            if(!jobInfo.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"An invalid job id was sent",assignJobRequest));


            //Assign job
            Optional<UserAccount> truckOwner = userAccountRepo.findById(assignJobRequest.getTruckOwnerId());

            if(truckOwner.isPresent()){
                if(truckOwner.get().getUserRole().getUserRoleName().equalsIgnoreCase(VariableUtil.TRUCK_OWNER)){
                    AssignJobInfo checkIfTruckOwnerHasAlreadyBeenAssignedJob = assignJobInfoRepo.findByAssignedToAndJobInfo(truckOwner.get(), jobInfo.get());

                    if(checkIfTruckOwnerHasAlreadyBeenAssignedJob == null)
                        return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"Truck owner has to show request before job can be assigned",assignJobRequest));

                    checkIfTruckOwnerHasAlreadyBeenAssignedJob.setAssigned(true);
                    checkIfTruckOwnerHasAlreadyBeenAssignedJob.setAssignedDate(new Date());
                    checkIfTruckOwnerHasAlreadyBeenAssignedJob.setTruckNumber(assignJobRequest.getNumOfTrucks());
                    assignJobInfoRepo.save(checkIfTruckOwnerHasAlreadyBeenAssignedJob);


                    //Create Load Codes And Completion Codes for trucks
                    List<LoadCompletionCodeManager> LoadCompletionCodeManagerList = loadCompletionCodeManagerRepo.findByAssignJobId(checkIfTruckOwnerHasAlreadyBeenAssignedJob.getId());
                    if(LoadCompletionCodeManagerList.size() >= assignJobRequest.getNumOfTrucks())
                        for(int i=0; i < assignJobRequest.getNumOfTrucks(); i++){
                            LoadCompletionCodeManager lccm = LoadCompletionCodeManagerList.get(i);
                            String loadCode = CommonUtil.getCode();
                            String completionCode = CommonUtil.getCode();

                            lccm.setCompletionCode(completionCode);
                            lccm.setLoadCode(loadCode);
                            loadCompletionCodeManagerRepo.save(lccm);

                            Optional<TruckInfo> truckInfo = truckInfoRepo.findById(lccm.getTruckId());
                            truckInfo.get().setTruckStatus(TruckEnum.UNAVAILABLE.getCode());
                            truckInfoRepo.save(truckInfo.get());

                            //Send Email To Load Owner
                            MailUtils mails = new MailUtils();
                            mails.setTo(jobInfo.get().getUserAccount().getUsername());
                            mails.setSubject("Newly Assigned Job To ==>"+truckOwner.get().getCompanyName());
                            mails.setName(jobInfo.get().getUserAccount().getCompanyName());
                            mails.setHtmlFileName(VariableUtil.ASSIGN_JOB_LOAD_OWNER_HTML_FILE);

                            Map<String, Object> mailProp = new HashMap<>();
                            mailProp.put(VariableUtil.RECEIVER_NAME, jobInfo.get().getUserAccount().getCompanyName());
                            mailProp.put("completionCode", completionCode);
                            mailProp.put("loadCode", loadCode);
                            mailProp.put("truckOwnerContact", truckOwner.get().getMobilePhone());
                            mailProp.put("companyName", truckOwner.get().getCompanyName());
                            mailProp.put("jobSummary", jobInfo.get().getJobSummary());
                            mails.setMailProps(mailProp);

                            mailService.sendEmail(mails,jobInfo.get().getUserAccount().isEmailNotification());
                        }

                    //Change job status to assigned
                    jobInfo.get().setJobStatus(JobEnum.ASSIGNED.getCode());
                    jobInfoRepo.save(jobInfo.get());


                    //User must have tuck owner role
                    //Send Email to truck owner asynchronously
                    //Build Mail Props
                    MailUtils mail = new MailUtils();
                    mail.setTo(truckOwner.get().getUsername());
                    mail.setSubject("Newly Assigned Job");
                    mail.setName(truckOwner.get().getCompanyName());
                    mail.setHtmlFileName(VariableUtil.ASSIGN_JOB_HTML_FILE);

                    Map<String, Object> mailProps = new HashMap<>();
                    mailProps.put(VariableUtil.RECEIVER_NAME, truckOwner.get().getCompanyName());
                    mailProps.put(VariableUtil.LOAD_OWNER_NAME, jobInfo.get().getUserAccount().getCompanyName());
                    mailProps.put(VariableUtil.QUANTITY, jobInfo.get().getTruckNo());
                    mailProps.put(VariableUtil.TRUCK_TYPE, jobInfo.get().getTruckType().getName());
                    mailProps.put(VariableUtil.MATERIAL, jobInfo.get().getLoadCategory().getName());
                    mailProps.put(VariableUtil.PICK_UP, jobInfo.get().getPickUpAddressCity() + "," + jobInfo.get().getPickUpAddressState());
                    mailProps.put(VariableUtil.DROP_OFF, jobInfo.get().getDeliveryAddressCity() + "," + jobInfo.get().getDeliveryAddressState());
                    mail.setMailProps(mailProps);

                    //send user a assign email
                    mailService.sendEmail(mail,truckOwner.get().isEmailNotification());

                    String message = String.format(VariableUtil.ASSIGN_JOB,truckOwner.get().getCompanyName(),
                            jobInfo.get().getUserAccount().getCompanyName());

                    //Send Sms
                    SmsUtils smsUtils = new SmsUtils("Aikapremium Job Assign Message",message,truckOwner.get().getMobilePhone(),"234");
                    smsService.sendSms(smsUtils,truckOwner.get().getId(),truckOwner.get().isSmsNotification(),truckOwner.get().isWhatsappNotification(),truckOwner.get().getWalletBalance());


                    String msg = String.format("%s (%s) just assigned a job to %s to move %s with %s %s from %s to %s",jobInfo.get().getUserAccount().getCompanyName(),
                            jobInfo.get().getUserAccount().getUsername(),truckOwner.get().getCompanyName(),jobInfo.get().getLoadCategory().getName(),
                            jobInfo.get().getTruckNo(),jobInfo.get().getTruckType().getName(),jobInfo.get().getPickUpAddress(),jobInfo.get().getDeliveryAddress());
                    smsService.sendAdminPushNotification(msg,VariableUtil.ASSIGN_JOB_SLACK_URL);

                    //Log Message
                    logMessage(jobInfo.get().getUserAccount().getId(), truckOwner.get().getId(), message, MessageTypeEnum.ASSIGN_JOB.getCode());

                    return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Job has been assigned successfully",assignJobRequest));
                }
            }
        }catch(Exception e){
            log.error("<<<<<<An error occurred while trying to assign job info request",e);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "Something went wrong,please contact your administrator",assignJobRequest));
    }




    /**
     * This method creates job
     * @param jobRequest contains job information
     * @return  ResponseEntity contains response
     */
    @Override
    public ResponseEntity createJob(CreateJobRequest jobRequest) {
        try{
            log.info("\n\n\n");
            log.info(">>>>>>>New Job Creation request from calling client::::"+jobRequest.toString());

            log.info(">>>>>>Validating parameters in job request");
            String jobRequestValidationRsp = ValidationUtil.validateCreateJobRequest(jobRequest);
            if(jobRequestValidationRsp != null){
                //Terminate request and return response back to calling client
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), jobRequestValidationRsp, jobRequest));
            }

            return populateJobInfoObjectForCreate(jobRequest);

        } catch (Exception e) {
            log.error("<<<<<<An error occurred while trying to create job:::"+e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), "Unable to process your request,try again later ", jobRequest));
    }





    /**
     * This method edits job
     * @param editJobRequest contains job information
     * @return  ResponseEntity contains response
     */
    public ResponseEntity editJob(EditJobRequest editJobRequest) {
        try{
            log.info("\n\n\n");
            log.info(">>>>>>>New Job Edit request from calling client::::"+editJobRequest.toString());

            log.info(">>>>>>Validating parameters in job request");
            String jobRequestValidationRsp = ValidationUtil.validateEditJobRequest(editJobRequest);
            if(jobRequestValidationRsp != null){
                //Terminate request and return response back to calling client
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), jobRequestValidationRsp, editJobRequest));
            }

            return populateJobInfoObjectForEdit(editJobRequest);

        } catch (Exception e) {
            log.error("<<<<<<An error occurred while trying to edit job:::"+e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), "Unable to process your request,try again later ", editJobRequest));
    }







    /**
     * This method helps to do review
     *
     * @param reviewRequest contains information needed to provide a review
     * @return
     */
    public ResponseEntity closeTask(CloseTaskRequest reviewRequest) {
        try{
            log.info("\n\n\n");
            log.info(">>>>>>>New Review request from calling client::::"+reviewRequest.toString());

            log.info(">>>>>>Validating parameters in review request");
            String reviewRequestValidationRsp = ValidationUtil.validateReviewRequest(reviewRequest);

            if(reviewRequestValidationRsp != null){
                //Terminate request and return response back to calling client
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), reviewRequestValidationRsp, reviewRequest));
            }

            //Populate userRatingLog
            UserRatingLog userRatingLog = new UserRatingLog();

            Optional<JobInfo> jobInfo = jobInfoRepo.findById(reviewRequest.getJobId());
            if(!jobInfo.isPresent()) {
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid jobId", reviewRequest));
            }

            Optional<UserAccount> userAccount = userAccountRepo.findById(reviewRequest.getUserId());

            if(!userAccount.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId",reviewRequest));
            userRatingLog.setUserAccount(userAccount.get());


            AssignJobInfo assign = assignJobInfoRepo.findByAssignedToAndJobInfo(userAccount.get(),jobInfo.get());

            LoadCompletionCodeManager lcm = loadCompletionCodeManagerRepo.findByTruckIdAndAssignJobId(reviewRequest.getTruckId(),assign.getId());

            if(!lcm.getStatus().equals(SettlementEnum.END_SETTLEMENT_REQUEST.getMessage()))
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.SETTLEMENT_ERROR.getCode(), "Truck owner has not been settled for this request", reviewRequest));


            userRatingLog.setJobInfo(jobInfo.get());
            if(reviewRequest.getComment() != null)  userRatingLog.setComment(reviewRequest.getComment());
            if(reviewRequest.getDealAmount() != null) userRatingLog.setDealAmount(reviewRequest.getDealAmount());
            if(userRatingLog.isFavourite())  userRatingLog.setFavourite(reviewRequest.isTruckOwnerFavourite());
            userRatingLog.setDateCreated(new Date());
            userRatingLog.setDateRated(new Date());

            if(reviewRequest.getUserRating() <= 5 && reviewRequest.getUserRating() > 0) userRatingLog.setUserRating(reviewRequest.getUserRating());

            //Get Number of task left to close job
            int numOfTaskLeft = assignJobInfoRepo.countByJobInfoAndIsAssignedAndIsTaskClosed(jobInfo.get(),true,false);

            //Close User On Assign Table
            if(numOfTaskLeft != 0) {

                //Send to database
                UserRatingLog saveRsp = userRatingLogRepo.save(userRatingLog);

                if(saveRsp == null)
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "System Malfunction", reviewRequest));


                AssignJobInfo assignJob = assignJobInfoRepo.findByJobInfoAndAssignedToAndIsAssignedAndIsTaskClosed(jobInfo.get(), userAccount.get(), true, false);
                assignJob.setClosedTaskDate(new Date());
                assignJob.setTaskClosed(true);
                assignJobInfoRepo.save(assignJob);

                if ((numOfTaskLeft - 1) == 0) {
                    jobInfo.get().setJobStatus(JobEnum.CLOSED.getCode());
                    jobInfoRepo.save(jobInfo.get());
                }

                String message = String.format(VariableUtil.JOB_REVIEW,userAccount.get().getCompanyName(),
                        jobInfo.get().getUserAccount().getCompanyName());

                //Send Sms
                SmsUtils smsUtils = new SmsUtils("Aikapremium Review Message",message,userAccount.get().getMobilePhone(),"234");
                smsService.sendSms(smsUtils,userAccount.get().getId(),userAccount.get().isSmsNotification(),userAccount.get().isWhatsappNotification(),userAccount.get().getWalletBalance());

                //Log Message
                logMessage(jobInfo.get().getUserAccount().getId(), userAccount.get().getId(), message, MessageTypeEnum.JOB_REVIEW.getCode());

                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Task Closed Successfully", reviewRequest));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "User cannot be rated since there is no pending task", reviewRequest));

        } catch (Exception e) {
            log.error("<<<<<<An error occurred while trying to provide review:::"+e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), "Unable to process your request,try again later ", reviewRequest));
    }




    /**
     * This method returns  all job info by Id
     * @return  ResponseEntity contains response
     */
    @Override
    public ResponseEntity<Object> getJobsById(Long jobId){
        try{
            Optional<JobInfo> j = jobInfoRepo.findById(jobId);
            if(j.isPresent()){
                Map<String,Object> jobInfo = returnJobInMapFormat(j.get(),null, VariableUtil.ORM);
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),jobInfo));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(),ResponseEnum.NO_RECORD_FOUND.getMessage(),jobId));
            }
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.SYSTEM_ERROR.getCode(),ResponseEnum.AN_ERROR_OCCURRED.getMessage(),jobId));
        }
    }

    public ResponseEntity getJobByUserId(Long userId) {
        try{
            Optional<UserAccount> userAccount = userAccountRepo.findById(userId);
            if(!userAccount.isPresent()){
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId", userId));
            }

            List<JobInfo> jobInfoList = jobInfoRepo.findByUserAccountOrderByDateCreatedDesc(userAccount.get());
            if(jobInfoList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(),ResponseEnum.NO_RECORD_FOUND.getMessage(),userId));
             }else{
                List<Object> result = new ArrayList<>();
                for (JobInfo j : jobInfoList) {
                    Map<String, Object> jobInfo =  returnJobInMapFormat(j,null, VariableUtil.ORM);
                    result.add(jobInfo);
                }
                return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), result));
            }
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.SYSTEM_ERROR.getCode(),ResponseEnum.AN_ERROR_OCCURRED.getMessage(),userId));
        }
    }

    /**
     * This method helps a user show interest
     * @param showInterest contains encrypted request
     * @return
     */
    public Map<String,Object> returnShowInterestPageInfo(String showInterest) {
        log.info("\n\n\n");
        log.info(">>>>>>>New Interest from calling client::::"+showInterest);

        String decryptedString = EncryptUtil.decrypt(showInterest,VariableUtil.SECRET);

        String[] parts = decryptedString.split("\\|");

        if(parts.length > 0){
            String jobId = parts[0];
            //Get JobInfo from the database
            Optional<JobInfo> jobInfo = jobInfoRepo.findById(Long.valueOf(jobId));

            Map<String,Object> result = new HashMap<>();
            result.put("hiddenReq",showInterest);
            if(jobInfo.isPresent()) {
                result.put("jobSummary", jobInfo.get().getJobSummary());
                result.put("pickUpAddress", String.format("%s,%s", jobInfo.get().getPickUpAddressCity(), jobInfo.get().getPickUpAddressState()));
                result.put("deliveryAddress", String.format("%s,%s", jobInfo.get().getDeliveryAddressCity(), jobInfo.get().getDeliveryAddressState()));
            }
            return result;
        }
        return null;
    }



    /**
     * This method returns jobs
     * @return  ResponseEntity contains response
     */
    public ResponseEntity<Object> findJobs(PaginationRequest paginationRequest) {

        List<Object[]> resultSet = paginationUtil.executePaginatedQuery(SqlQuery.FIND_JOB_QUERY,paginationRequest);

        if(resultSet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(), paginationRequest));
        }

        //Declare variables for result
        int totalRecords = 0;
        int counter = 0;
        List<Object> result = new ArrayList<>();

        //Loop through result set
        for(Object[] object: resultSet){
            Map<String,Object> jobInfo = returnJobInMapFormat(null,object, VariableUtil.NATIVE);

            if(counter == 0) {
                totalRecords = Integer.parseInt(object[16].toString());
            }

            result.add(jobInfo);
            counter++;
        }
        PaginationResult paginationResult = new PaginationResult();
        paginationResult.setRecordsTotal(totalRecords);
        paginationResult.setPageNumber(paginationRequest.getPageNumber());
        paginationResult.setPageSize(paginationRequest.getPageSize());
        paginationResult.setResults(result);
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), paginationResult));
    }


    /**
     * This method returns jobs
     *
     * @return ResponseEntity contains response
     */
    public ResponseEntity<Object> getJobs(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC,sortBy));

        Page<JobInfo> jobInfos = jobInfoRepo.findAll(paging);
        if(!jobInfos.isEmpty()){
            List<Object> result = new ArrayList<>();
            for (JobInfo j : jobInfos.getContent()){
                Map<String, Object> jobInfo =  returnJobInMapFormat(j,null, VariableUtil.ORM);
                result.add(jobInfo);
            }

            PaginationResult paginationResult = new PaginationResult();
            Long totalRecord = jobInfos.getTotalElements();
            paginationResult.setRecordsTotal(totalRecord.intValue());
            paginationResult.setPageNumber(pageNo);
            paginationResult.setPageSize(pageSize);
            paginationResult.setResults(result);
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), paginationResult));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(), null));
    }


    /**
     * This method returns load level types
     *
     * @return ResponseEntity contains response
     */
    public ResponseEntity<Object> getLoadLevelTypes() {
        List<LoadLevel> loadLevels = loadLevelRepo.findAll();
        if(!loadLevels.isEmpty()){
            List<Object> result = new ArrayList<>();
            for (LoadLevel l : loadLevels) {
                Map<String, Object> loadLevel = new HashMap<>();
                loadLevel.put("id", l.getId());
                loadLevel.put("name", l.getName());
                result.add(loadLevel);
            }
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), result));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(), null));
    }




    /**
     * This method returns load categories
     * @return  ResponseEntity contains response
     */
    public ResponseEntity<Object> getLoadCategories() {
        List<LoadCategory> loadCategories = loadCategoryRepo.findAll();
        if (!loadCategories.isEmpty()) {
            List<Object> result = new ArrayList<>();
            for (LoadCategory l : loadCategories) {
                Map<String, Object> loadCategory = new HashMap<>();
                loadCategory.put("id", l.getId());
                loadCategory.put("name", l.getName());
                result.add(loadCategory);
            }
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), result));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(), null));
    }




    /**
     * This method returns truck types
     * @return  ResponseEntity contains response
     */
    public ResponseEntity<Object> getTruckTypes() {
        List<TruckType> truckTypes = truckTypeRepo.findAll();
        if (!truckTypes.isEmpty()) {
            List<Object> result = new ArrayList<>();
            for(TruckType t : truckTypes){
                Map<String, Object> truckType = new HashMap<>();
                truckType.put("id", t.getId());
                truckType.put("name", t.getName());
                result.add(truckType);
            }
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), result));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(), null));

    }




    private ResponseEntity<Object> populateJobInfoObjectForCreate(CreateJobRequest jobRequest){
        //Create job info object
        JobInfo jobInfo = new JobInfo();
        jobInfo.setDeliveryAddressCity(jobRequest.getDropOffAddressCity());
        jobInfo.setDeliveryAddressState(jobRequest.getDropOffAddressState());
        jobInfo.setDeliveryAddress(jobRequest.getDropOffAddressCity()+","+jobRequest.getDropOffAddressState());
        jobInfo.setPickUpAddressCity(jobRequest.getPickUpAddressCity());
        jobInfo.setPickUpAddressState(jobRequest.getPickUpAddressState());
        jobInfo.setPickUpAddress(jobRequest.getPickUpAddressCity()+","+jobRequest.getPickUpAddressState());
        jobInfo.setTruckNo(jobRequest.getNumberOfTrucks());
        jobInfo.setJobSummary(jobRequest.getJobSummary());
        jobInfo.setJobStatus(JobEnum.ACTIVE.getCode());

        //Populate Type of material
        Optional<LoadCategory> loadCategory = loadCategoryRepo.findById(jobRequest.getMaterialType());
        if(!loadCategory.isPresent()){
            //Terminate request and send invalid request back
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid material type id sent", jobRequest));
        }
        jobInfo.setLoadCategory(loadCategory.get());


        //Populate Load Level
        Optional<LoadLevel> loadLevel = loadLevelRepo.findById(jobRequest.getLoadLevel());
        if(!loadLevel.isPresent()){
            //Terminate request and send invalid request back
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid load level id sent", jobRequest));
        }
        jobInfo.setLoadLevel(loadLevel.get());


        //Populate Truck Type
        Optional<TruckType> truckType = truckTypeRepo.findById(jobRequest.getTruckType());
        if(!truckType.isPresent()){
            //Terminate request and send invalid request back
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid truck type id sent", jobRequest));
        }
        jobInfo.setTruckType(truckType.get());
        jobInfo.setJobReferenceNumber(new SimpleDateFormat("ddMMyyhhss").format(new Date()));
        jobInfo.setRemoved(false);


        //Get user creating the job from the database
        Optional<UserAccount> userAccount = userAccountRepo.findById(jobRequest.getUserId());
        if(!userAccount.isPresent()){
            //Terminate request and send invalid request back
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid user Id sent", jobRequest));
        }
        jobInfo.setUserAccount(userAccount.get());
        jobInfo.setDateCreated(new Date());


        //Check pick up date type
        Date pickUpDate = CommonUtil.formatDate(jobRequest.getPickUpDate());
        if(pickUpDate == null) {
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid pickupdate Format", jobRequest));
        }
        jobInfo.setFixedPickUpDate(pickUpDate);

        //persist new job object in the database
        JobInfo saveJobInfoRsp = jobInfoRepo.save(jobInfo);

        if(saveJobInfoRsp != null) {

            //Send Email To All Users in the table who are truckOwners and have similar routes
            mailService.sendTruckOwnersEmail(saveJobInfoRsp);

            String msg = String.format("%s(%s) needs %s %s to move %s from %s to %s  ======>%s",userAccount.get().getCompanyName(),userAccount.get().getUsername(),jobRequest.getNumberOfTrucks(),
                    truckType.get().getName(),loadCategory.get().getName(),jobInfo.getPickUpAddress(),jobInfo.getDeliveryAddress(),jobInfo.getJobReferenceNumber());
            smsService.sendAdminPushNotification(msg,VariableUtil.CREATED_JOB_SLACK_URL);

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),returnJobInMapFormat(saveJobInfoRsp,null,VariableUtil.ORM)));
        }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OPERATION_FAILED.getCode(),"System was unable to carry operation",jobRequest));
    }

    /**
     * This method returns all jobs of interest by userId
     * @param userId unique identifier for a user
     * @return ResponseEntity contains response information
     */
    public ResponseEntity getJobsOfInterestByUserId(Long userId) {

        //Get User Info from the database
        Optional<UserAccount> userAccount = userAccountRepo.findById(userId);

        if(!userAccount.isPresent())
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId",userId));

        List<AssignJobInfo> assignJobInfos =  assignJobInfoRepo.findByAssignedTo(userAccount.get());
        if(assignJobInfos.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(),userId));

        List<Object> results = new ArrayList<>();
        for(AssignJobInfo a: assignJobInfos){
            Map<String,Object> result = returnJobInMapFormat(a.getJobInfo(),null,VariableUtil.ORM);
            results.add(result);
        }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), results));
    }


    /**
     * This method returns all interested users by jobId
     * @param jobId unique identifier for a job
     * @return ResponseEntity contains response information
     */
    public ResponseEntity getInterestedUsersByJobId(Long jobId) {
        //Get User Info from the database
        Optional<JobInfo> jobInfo = jobInfoRepo.findById(jobId);

        if(!jobInfo.isPresent())
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId",jobInfo));

        List<AssignJobInfo> assignJobInfos =  assignJobInfoRepo.findByJobInfo(jobInfo.get());
        if(assignJobInfos.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(),jobId));

        List<Object> results = new ArrayList<>();
        for(AssignJobInfo a: assignJobInfos){
            Map<String,Object> result = userService.returnUserAccountInMapFormat(a.getAssignedTo());
            result.put("interestDate",a.getCreatedDate());
            result.put("offerAmount",a.getOfferAmount());
            result.put("numberOfTrucks",a.getTruckNumber());
            result.put("expectedDeliveryDays",a.getExpectedDeliveryDays());
            result.put("assignStatus",a.isAssigned());
            results.add(result);
        }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), results));
    }



    public ResponseEntity getJobAssignedUsersByJobId(Long jobId) {
        //Get User Info from the database
        Optional<JobInfo> jobInfo = jobInfoRepo.findById(jobId);

        if(!jobInfo.isPresent())
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid jobId",jobInfo));

        List<AssignJobInfo> assignJobInfos =  assignJobInfoRepo.findByJobInfoAndIsAssignedAndIsTaskClosed(jobInfo.get(),true,false);

        if(assignJobInfos.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(),jobId));


        List<Object> results = new ArrayList<>();

        for(AssignJobInfo a: assignJobInfos){
            Map<String,Object> result = userService.returnUserAccountInMapFormat(a.getAssignedTo());
            result.put(VariableUtil.ASSIGNED_DATE,a.getAssignedDate());
            result.put(VariableUtil.NUMBER_OF_TASK_LEFT,assignJobInfos.size()-1);
            result.put(VariableUtil.ASSIGN_JOB_STATUS,returnStatus(jobInfo.get().getJobStatus()));
            List<LoadCompletionCodeManager> lccmList = loadCompletionCodeManagerRepo.findByAssignJobId(a.getId());
            List<Object> lccms = new ArrayList<>();
            for(LoadCompletionCodeManager lccm:lccmList){
                if(StringUtils.isNotBlank(lccm.getLoadCode())) {
                    Map<String, Object> res = new HashMap<>();
                    Optional<TruckInfo> truckInfo = truckInfoRepo.findById(lccm.getTruckId());
                    res.put("truckId", truckService.returnTruckInMapFormat(truckInfo.get(), null, VariableUtil.ORM));
                    res.put("loadCode", lccm.getLoadCode());
                    res.put("isLoadCodeUsed", CommonUtil.resolveCodeStatus(VariableUtil.LOAD_CODE,lccm.getStatus()));
                    res.put("isCompletionCodeUsed", CommonUtil.resolveCodeStatus(VariableUtil.COMPLETION_CODE,lccm.getStatus()));
                    res.put("completionCode", lccm.getCompletionCode());
                    res.put("createdDate", lccm.getCreatedDate());
                    res.put("jobInfo", returnJobInMapFormat(jobInfo.get(), null, VariableUtil.ORM));
                    lccms.add(res);
                }
            }
            result.put("loadCompletionManager",lccms);
            result.put("expectedDeliveryDays",a.getExpectedDeliveryDays());
            result.put("offerAmount",a.getOfferAmount());
            result.put("assignedTruckNumber",a.getAssignedTruckNumber());
            result.put("truckNumber",a.getTruckNumber());
            results.add(result);
        }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), results));
    }




    public ResponseEntity getAssignedJobByUserId(Long userId) {
        //Get User Info from the database
        Optional<UserAccount> userAccount = userAccountRepo.findById(userId);

        if(!userAccount.isPresent())
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid userId",userId));

        List<AssignJobInfo> assignJobInfos =  assignJobInfoRepo.findByAssignedToAndIsAssignedAndIsTaskClosed(userAccount.get(),true,false);

        if(assignJobInfos.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(),userId));


        List<Object> results = new ArrayList<>();

        for(AssignJobInfo a: assignJobInfos){
            Map<String,Object> result = userService.returnUserAccountInMapFormat(a.getAssignedTo());
            result.put(VariableUtil.ASSIGNED_DATE,a.getAssignedDate());
            result.put(VariableUtil.NUMBER_OF_TASK_LEFT,assignJobInfos.size()-1);
            result.put(VariableUtil.ASSIGN_JOB_STATUS,returnStatus(a.getJobInfo().getJobStatus()));
            List<LoadCompletionCodeManager> lccmList = loadCompletionCodeManagerRepo.findByAssignJobId(a.getId());
            List<Object> lccms = new ArrayList<>();
            for(LoadCompletionCodeManager lccm:lccmList){
                if(StringUtils.isNotBlank(lccm.getLoadCode())) {
                    if(!lccm.getAdminStatus().equals(AdminSettlementStatusEnum.COMPLETED.getMessage())) {
                      Map<String, Object> res = new HashMap<>();
                        Optional<TruckInfo> truckInfo = truckInfoRepo.findById(lccm.getTruckId());
                        res.put("truckId", truckService.returnTruckInMapFormat(truckInfo.get(), null, VariableUtil.ORM));
                        res.put("loadCode", lccm.getLoadCode());
                        res.put("completionCode", lccm.getCompletionCode());
                        res.put("status", lccm.getAdminStatus());
                        res.put("isLoadCodeUsed", CommonUtil.resolveCodeStatus(VariableUtil.LOAD_CODE, lccm.getStatus()));
                        res.put("isCompletionCodeUsed", CommonUtil.resolveCodeStatus(VariableUtil.COMPLETION_CODE, lccm.getStatus()));
                        res.put("createdDate", lccm.getCreatedDate());
                        res.put("jobInfo", returnJobInMapFormat(a.getJobInfo(), null, VariableUtil.ORM));
                        lccms.add(res);
                    }
                }
            }
            result.put("loadCompletionManager",lccms);
            result.put("expectedDeliveryDays",a.getExpectedDeliveryDays());
            result.put("offerAmount",a.getOfferAmount());
            result.put("assignedTruckNumber",a.getAssignedTruckNumber());
            result.put("truckNumber",a.getTruckNumber());
            results.add(result);
        }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), results));
    }


    private static String returnStatus(int jobStatus){
        switch(jobStatus){
            case 1: return JobEnum.PROMOTED.getMessage();
            case 2: return JobEnum.ACTIVE.getMessage();
            case 3: return JobEnum.ASSIGNED.getMessage();
            case 4: return JobEnum.CLOSED.getMessage();
            case 5: return JobEnum.CANCELLED.getMessage();
            case 6: return JobEnum.PENDING_APPROVAL.getMessage();
            default: return null;
        }
    }

    /**
     * This helps manage edit of job
     *
     * @param jobRequest
     * @return
     */
    private ResponseEntity populateJobInfoObjectForEdit(EditJobRequest jobRequest){

        //Edit job info object
        Optional<JobInfo>  jobInfo = jobInfoRepo.findById(jobRequest.getJobId());
        if(!jobInfo.isPresent())
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid JobId sent", jobRequest));

        //Validate who's trying edit job
        Optional<UserAccount> userAccount = userAccountRepo.findById(jobRequest.getUserId());
        if(!userAccount.isPresent())  return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid user Id sent", jobRequest));

        //Checking if user has the right perform an audit or not
        boolean hasRights = ValidationUtil.validateUserRightsOnJobEdit(userAccount.get(),jobInfo.get(),jobRequest.getUserId());

        if(!hasRights){
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "User Id does not have right to perform this operation", jobRequest));
        }


        if (jobRequest.getDropOffAddressCity() != null && jobRequest.getDropOffAddressState() != null) {
            jobInfo.get().setDeliveryAddressCity(jobRequest.getDropOffAddressCity());
            jobInfo.get().setDeliveryAddressState(jobRequest.getDropOffAddressState());
            jobInfo.get().setDeliveryAddress(jobRequest.getDropOffAddressCity()+","+jobRequest.getDropOffAddressState());
        }


        if (jobRequest.getPickUpAddressCity() != null && jobRequest.getPickUpAddressState() != null) {
            jobInfo.get().setPickUpAddressCity(jobRequest.getPickUpAddressCity());
            jobInfo.get().setPickUpAddressState(jobRequest.getPickUpAddressState());
            jobInfo.get().setPickUpAddress(jobRequest.getPickUpAddressCity()+","+jobRequest.getPickUpAddressState());
        }


        if (jobRequest.getNumberOfTrucks() != 0) jobInfo.get().setTruckNo(jobRequest.getNumberOfTrucks());
        if (jobRequest.getJobSummary() != null) jobInfo.get().setJobSummary(jobRequest.getJobSummary());

        if (jobRequest.getJobStatus() == JobEnum.ACTIVE.getCode() || jobRequest.getJobStatus() == JobEnum.PROMOTED.getCode() || jobRequest.getJobStatus() == JobEnum.ASSIGNED.getCode() || jobRequest.getJobStatus() == JobEnum.CLOSED.getCode() || jobRequest.getJobStatus() == JobEnum.CANCELLED.getCode())
            jobInfo.get().setJobStatus(jobRequest.getJobStatus());

        //Populate Type of material
        if (jobRequest.getMaterialType() != null) {
            Optional<LoadCategory> loadCategory = loadCategoryRepo.findById(jobRequest.getMaterialType());
            if (!loadCategory.isPresent()) {
                //Terminate request and send invalid request back
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid material type id sent", jobRequest));
            }
            jobInfo.get().setLoadCategory(loadCategory.get());
        }

        //Populate Load Level
        if (jobRequest.getLoadLevel() != 0) {
            Optional<LoadLevel> loadLevel = loadLevelRepo.findById(jobRequest.getLoadLevel());
            if (!loadLevel.isPresent()) {
                //Terminate request and send invalid request back
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid load level id sent", jobRequest));
            }
            jobInfo.get().setLoadLevel(loadLevel.get());
        }

        //Populate Truck Type
        if (jobRequest.getTruckType() != 0) {
            Optional<TruckType> truckType = truckTypeRepo.findById(jobRequest.getTruckType());
            if (!truckType.isPresent()) {
                //Terminate request and send invalid request back
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid truck type id sent", jobRequest));
            }
            jobInfo.get().setTruckType(truckType.get());
        }


        //Check pick up date type
        if (jobRequest.getPickUpDate() != null) {
            Date pickUpDate = CommonUtil.formatDate(jobRequest.getPickUpDate());
            if (pickUpDate == null) {
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid pickupdate Format", jobRequest));
            }
            jobInfo.get().setFixedPickUpDate(pickUpDate);
        }

        //persist new job object in the database
        JobInfo saveJobInfoRsp = jobInfoRepo.save(jobInfo.get());

        if (saveJobInfoRsp != null) {
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), returnJobInMapFormat(saveJobInfoRsp, null, VariableUtil.ORM)));
        }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "System was unable to carry operation", jobRequest));
    }




    public Map<String,Object> returnJobInMapFormat(JobInfo jobInfo,Object[] object, String type){
        Map<String,Object> result = new HashMap<>();

        if(type.equals(VariableUtil.ORM)) {
            result.put("jobId", jobInfo.getId());
            result.put("jobReferenceNumber", jobInfo.getJobReferenceNumber());
            result.put("dropOffAddressState", jobInfo.getDeliveryAddressState());
            result.put("dropOffAddressCity", jobInfo.getDeliveryAddressCity());
            result.put("pickUpAddressState", jobInfo.getPickUpAddressState());
            result.put("pickUpAddressCity", jobInfo.getPickUpAddressCity());
            result.put("materialType", jobInfo.getLoadCategory());
            result.put("truckType", jobInfo.getTruckType());
            result.put("jobSummary", jobInfo.getJobSummary());
            result.put("loadLevel",jobInfo.getLoadLevel());
            result.put("truckNo",jobInfo.getTruckNo());
            result.put("jobStatus",jobInfo.getJobStatus());
            result.put("pickUpDate",jobInfo.getFixedPickUpDate());
            result.put("createdDate", jobInfo.getDateCreated());
            result.put("pickUpAddress",jobInfo.getPickUpAddress());
            result.put("deliveryAddress",jobInfo.getDeliveryAddress());
            result.put("userId", userService.returnUserAccountInMapFormat(jobInfo.getUserAccount()));
            int showInterestRsp = assignJobInfoRepo.countByJobInfo(jobInfo);
            result.put("showInterestCount",showInterestRsp);
            try {
                result.put("truckNo", jobInfo.getTruckNo());
            } catch (Exception e) {

            }
            result.put("pickUpDate", jobInfo.getFixedPickUpDate());
        }else{
            result.put("jobId",object[0]);
            result.put("jobReferenceNumber",object[1]);
            result.put("dropOffAddressState",object[2]);
            result.put("dropOffAddressCity",object[3]);
            result.put("pickUpAddressState",object[4]);
            result.put("pickUpAddressCity",object[5]);

            Map<String, Object> materialType = new HashMap<>();
            materialType.put("materialTypeId",object[6]);
            materialType.put("materialTypeName",object[7]);
            result.put("materialType",materialType);

            Map<String, Object> truckType = new HashMap<>();
            truckType.put("truckTypeId",object[8]);
            truckType.put("truckTypeName",object[9]);
            result.put("truckType",truckType);

            result.put("jobSummary",object[10]);
            result.put("createdDate",object[11]);
            result.put("pickUpDate",object[12]);
            result.put("truckNo",object[13]);

            Map<String, Object> userId = new HashMap<>();
            userId.put("userId",object[14]);
            userId.put("companyName",object[15]);
            userId.put("userProfileUrl",object[21]);

            userId.put("userStatus",object[17]);
            result.put("userId",userId);
            result.put("jobStatus",object[18]);
            result.put("pickUpAddress",object[22]);
            result.put("deliveryAddress",object[23]);

            Map<String, Object> loadLevel = new HashMap<>();
            loadLevel.put("loadLevelId",object[19]);
            loadLevel.put("loadLevelName",object[20]);
            result.put("loadLevel",loadLevel);
        }
        return result;
    }







    private AssignJobInfo populateAssignJob(UserAccount truckOwner, JobInfo jobInfo){
        AssignJobInfo assignJobInfo = new AssignJobInfo();
        assignJobInfo.setAssignedTo(truckOwner);
        assignJobInfo.setJobInfo(jobInfo);
        assignJobInfo.setAssigned(false);
        assignJobInfo.setTaskClosed(false);
        assignJobInfo.setAssignedDate(null);
        assignJobInfo.setClosedTaskDate(null);
        assignJobInfo.setCreatedDate(new Date());
        return assignJobInfo;
    }

    private String validateAssignJobRequest(AssignJobRequest assignJobRequest) {
        if(assignJobRequest.getTruckOwnerId() == null)
            return "truckOwnerId cannot be empty or null";
        return ResponseEnum.OK.getCode();
    }


    private void logMessage(long senderId,long recipientId,String message, int messageType) {
        try {
            //Log message to the database
            MessageLog messageLog = new MessageLog();
            messageLog.setCreatedDate(new Date());
            messageLog.setLoggerId(senderId);
            messageLog.setRecipientId(recipientId);
            messageLog.setMessage(message);
            messageLog.setMessageType(messageType);
            messageLogRepo.save(messageLog);
        }catch(Exception e){
            log.error("An error occurred while trying to log message::{}",e);
        }
    }

}
