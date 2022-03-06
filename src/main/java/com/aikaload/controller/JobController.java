package com.aikaload.controller;

import com.aikaload.asyncservice.AuditTrailService;
import com.aikaload.dto.*;
import com.aikaload.entity.AuditTrail;
import com.aikaload.service.JobService;
import com.aikaload.utils.EncryptUtil;
import com.aikaload.utils.VariableUtil;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Date;


@Controller
@RequestMapping("/job")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JobController {

    private final JobService jobService;
    private final AuditTrailService auditTrailService;

    /**
     * This method creates job
     * @param createJobRequest contains job information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/create-job")
    public ResponseEntity  createJob(@RequestBody CreateJobRequest createJobRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"CREATE_JOB",createJobRequest.toString(),new Date()));
        return jobService.createJob(createJobRequest);
    }



    /**
     * This method edits job
     * @param editJobRequest contains job information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/edit-job")
    public ResponseEntity  editJob(@RequestBody EditJobRequest editJobRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_JOB",editJobRequest.toString(),new Date()));
        return jobService.editJob(editJobRequest);
    }



    /**
     * This method assigns job
     * @param assignJobRequest contains job information to be assigned
     * @return  ResponseEntity contains response
     */
    @PostMapping("/assign-job")
    public ResponseEntity  assignJob(@RequestBody AssignJobRequest assignJobRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"ASSIGN_JOB",assignJobRequest.toString(),new Date()));
        return jobService.assignJob(assignJobRequest);
    }


    /**
     * This method helps to close a task
     * @param reviewRequest contains review information to be assigned
     * @return  ResponseEntity contains response
     */
    @PostMapping("/close-task")
    public ResponseEntity  closeTask(@RequestBody CloseTaskRequest reviewRequest, Authentication authentication) {
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"CLOSE_TASK",reviewRequest.toString(),new Date()));
        return jobService.closeTask(reviewRequest);
    }


    /**
     * This method returns load levels
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-load-levels")
    public ResponseEntity  getLoadLevels() {
        return jobService.getLoadLevelTypes();
    }


    /**
     * This method returns jobs
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-jobs")
    public ResponseEntity  getJobs(@RequestParam(defaultValue = "0") Integer pageNo,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "id") String sortBy) {
        return jobService.getJobs(pageNo,pageSize,sortBy);
    }


    /**
     * This method returns jobs
     * @return  ResponseEntity contains response
     */
    @PostMapping("/find-jobs")
    public ResponseEntity  findJobs(@RequestBody PaginationRequest paginationRequest) {
        return jobService.findJobs(paginationRequest);
    }


    /**
     * This method returns material types
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-material-types")
    public ResponseEntity  getMaterialTypes() {
        return jobService.getLoadCategories();
    }



    /**
     * This method returns truck types
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-truck-types")
    public ResponseEntity  getTruckType() {
        return jobService.getTruckTypes();
    }



    /**
     * This method returns job by Id
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-jobs-by-id/{jobId}")
    @ApiParam(name =  "jobId", type = "Long", value = "Unique Identifier for a job (pass valid jobId)", example = "32", required = true)
    public ResponseEntity  getJobById(@PathVariable Long jobId) {
        return jobService.getJobsById(jobId);
    }

    /**
     * This method returns job by Id
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-jobs-by-userId/{userId}")
    public ResponseEntity  getJobByUserId(@PathVariable Long userId) {
        return jobService.getJobByUserId(userId);
    }


    /**
     * This method returns all jobs user has shown interest by yserId
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-jobs-of-interest-by-user-id/{userId}")
    @ApiParam(name =  "userId", type = "Long", value = "This takes in userId and returns all the Jobs the user has shown interest in (this requires a valid userId)", example = "30", required = true)
    public ResponseEntity  getJobsOfInterestByUserId(@PathVariable Long userId) {
        return jobService.getJobsOfInterestByUserId(userId);
    }

    /**
     * This method returns all jobs user has shown interest by yserId
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-assigned-jobs-by-user-id/{userId}")
    @ApiParam(name =  "userId", type = "Long", value = "This takes in userId and returns all the Jobs assigned to that user (this requires a valid userId)", example = "30", required = true)
    public ResponseEntity  getAssignedJobByUserId(@PathVariable Long userId) {
        return jobService.getAssignedJobByUserId(userId);
    }


    /**
     * This method returns all interest users by jobId
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-interested-users-by-job-id/{jobId}")
    @ApiParam(name =  "jobId", type = "Long", value = "A valid jobId helps return all users interested in the job", example = "32", required = true)
    public ResponseEntity  getInterestedUsersByJobId(@PathVariable Long jobId) {
        return jobService.getInterestedUsersByJobId(jobId);
    }


    /**
     * This method returns all assigned job users by jobId
     * @return  ResponseEntity contains response
     */
    @GetMapping("/get-job-assigned-users-by-jobId/{jobId}")
    public ResponseEntity  getJobAssignedUsersByJobId(@PathVariable Long jobId) {
        return jobService.getJobAssignedUsersByJobId(jobId);
    }


    /**
     * This method handles interest by jobUsers
     * @return  ResponseEntity contains response
     */
    @PostMapping("/show-interest")
    public ResponseEntity showInterest(@RequestBody ShowInterestRequest showInterestRequest, Authentication authentication){
        //auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"SHOW_INTEREST",String.format("TruckOwnerId::%s|JobId::%s",showInterestRequest.getTruckOwnerId(),showInterestRequest.getJobId()),new Date()));
        if(StringUtils.isNotBlank(showInterestRequest.getEncryptedRequest())){
            String decryptedString = EncryptUtil.decrypt(showInterestRequest.getEncryptedRequest(), VariableUtil.SECRET);
            String[] parts = decryptedString.split("\\|");
            if (parts.length > 0) {
                showInterestRequest.setJobId(Long.valueOf(parts[0]));
                showInterestRequest.setTruckOwnerId(Long.valueOf(parts[1]));
            }
        }
        return jobService.showInterest(showInterestRequest);
    }


}
