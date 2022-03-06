package com.aikaload.controller;

import com.aikaload.asyncservice.AuditTrailService;
import com.aikaload.dto.CreateTruckTypeRequest;
import com.aikaload.dto.EditTruckTypeRequest;
import com.aikaload.entity.AuditTrail;
import com.aikaload.service.TruckTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@RestController
@RequestMapping("/truck-type")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TruckTypeController {

    private final TruckTypeService truckTypeService;
    private final AuditTrailService auditTrailService;

    /**
     * This method creates truck type
     * @param createTruckTypeRequest contains truck type information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/create-truck-type")
    public ResponseEntity createTruckType(@RequestBody CreateTruckTypeRequest createTruckTypeRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"CREATE_TRUCK_TYPE",createTruckTypeRequest.toString(),new Date()));
        return truckTypeService.createTruckType(createTruckTypeRequest);
    }

    /**
     * This method edits truck type
     * @param editTruckTypeRequest contains truck type information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/edit-truck-type")
    public ResponseEntity editTruckType(@RequestBody EditTruckTypeRequest editTruckTypeRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_TRUCK_TYPE",editTruckTypeRequest.toString(),new Date()));
        return truckTypeService.editTruckType(editTruckTypeRequest);
    }


    @GetMapping("/get-truck-type-by-id/{truckTypeId}")
    public ResponseEntity getTruckById(@PathVariable int truckTypeId){
        return truckTypeService.getTruckTypeById(truckTypeId);
    }


    @GetMapping("/remove-truck-type-by-id/{truckTypeId}")
    public ResponseEntity removeTruckById(@PathVariable int truckTypeId, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"REMOVE_TRUCK_TYPE_BY_ID",String.valueOf(truckTypeId),new Date()));
        return truckTypeService.removeTruckTypeById(truckTypeId);
    }

    @GetMapping("/get-truck-types")
    public ResponseEntity getTruckTypes(){
        return truckTypeService.getTruckTypes();
    }
}
