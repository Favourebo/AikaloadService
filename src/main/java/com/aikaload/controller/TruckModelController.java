package com.aikaload.controller;

import com.aikaload.asyncservice.AuditTrailService;
import com.aikaload.dto.CreateTruckModelRequest;
import com.aikaload.dto.EditTruckModelRequest;
import com.aikaload.entity.AuditTrail;
import com.aikaload.service.TruckModelService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/truck-model")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TruckModelController {

    private final TruckModelService truckModelService;
    private final AuditTrailService auditTrailService;

    /**
     * This method creates truck model
     * @param createTruckModelRequest contains truck model information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/create-truck-model")
    public ResponseEntity createTruckModel(@RequestBody CreateTruckModelRequest createTruckModelRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"CREATE_TRUCK_MODEL",createTruckModelRequest.toString(),new Date()));
        return truckModelService.createTruckModel(createTruckModelRequest);
    }

    /**
     * This method edits truck model
     * @param editTruckModelRequest contains truck model information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/edit-truck-model")
    public ResponseEntity editTruckModel(@RequestBody EditTruckModelRequest editTruckModelRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_TRUCK_TYPE",editTruckModelRequest.toString(),new Date()));
        return truckModelService.editTruckModel(editTruckModelRequest);
    }


    @GetMapping("/get-truck-model-by-id/{truckModelId}")
    public ResponseEntity getTruckModelById(@PathVariable int truckModelId){
        return truckModelService.getTruckModelById(truckModelId);
    }


    @GetMapping("/remove-truck-model-by-id/{truckModelId}")
    public ResponseEntity removeTruckModelById(@PathVariable int truckModelId, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"REMOVE_TRUCK_TYPE_BY_ID",String.valueOf(truckModelId),new Date()));
        return truckModelService.removeTruckModelById(truckModelId);
    }

    @GetMapping("/get-truck-models")
    public ResponseEntity getTruckModels(){
        return truckModelService.getTruckModels();
    }
}
