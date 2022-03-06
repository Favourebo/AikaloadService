package com.aikaload.serviceInterface;

import com.aikaload.dto.CreateJobRequest;
import org.springframework.http.ResponseEntity;

public interface JobInterface {
    ResponseEntity createJob(CreateJobRequest jobRequest);
    ResponseEntity getJobsById(Long jobId);
    ResponseEntity getLoadLevelTypes();
    ResponseEntity getLoadCategories();
    ResponseEntity getTruckTypes();
}
