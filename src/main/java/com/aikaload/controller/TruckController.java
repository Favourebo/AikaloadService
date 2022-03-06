package com.aikaload.controller;

import com.aikaload.asyncservice.AuditTrailService;
import com.aikaload.dto.PaginationRequest;
import com.aikaload.entity.AuditTrail;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.aikaload.dto.TruckRequest;
import com.aikaload.service.TruckService;
import java.util.Date;


@RestController
@RequestMapping("/truck")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TruckController {

    private final TruckService truckService;
    private final AuditTrailService auditTrailService;

    /**
     * This method creates truck
     * @param truckRequest contains truck information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/create-truck")
    public ResponseEntity createTruck(@RequestBody TruckRequest truckRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"CREATE_TRUCK",truckRequest.toString(),new Date()));
        return truckService.createTruck(truckRequest);
    }

    /**
     * This method edits truck
     * @param truckRequest contains truck information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/edit-truck")
    public ResponseEntity editTruck(@RequestBody TruckRequest truckRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_TRUCK",truckRequest.toString(),new Date()));
        return truckService.editTruck(truckRequest);
    }


    @GetMapping("/get-truck-models")
    public ResponseEntity getTruckModels(){
        return truckService.getTruckModels();
    }


    @GetMapping("/get-trucks")
    public ResponseEntity getTrucks(@RequestParam(defaultValue = "0") Integer pageNo,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(defaultValue = "id") String sortBy){
        return truckService.getTrucks(pageNo,pageSize,sortBy);
    }

    @GetMapping("/get-trucks-without-pagination")
    public ResponseEntity getTrucksWithoutPagination(){
        return truckService.getTrucksWithoutPagination();
    }

    @PostMapping("/find-trucks")
    public ResponseEntity findTrucks(@RequestBody PaginationRequest paginationRequest){
        return truckService.findTrucks(paginationRequest);
    }

    @GetMapping("/get-truck-by-id/{truckId}")
    public ResponseEntity getTruckById(@PathVariable int truckId){
        return truckService.getTruckById(truckId);
    }

    @GetMapping("/get-truck-by-userId/{userId}")
    public ResponseEntity getTruckByuserId(@PathVariable Long userId){
        return truckService.getTruckByUserId(userId);
    }
}
