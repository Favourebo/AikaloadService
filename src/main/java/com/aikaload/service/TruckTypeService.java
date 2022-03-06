package com.aikaload.service;

import com.aikaload.dto.CreateTruckTypeRequest;
import com.aikaload.dto.EditTruckTypeRequest;
import com.aikaload.dto.Response;
import com.aikaload.entity.TruckType;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.repo.TruckTypeRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class TruckTypeService {
    private final TruckTypeRepo truckTypeRepo;

    public ResponseEntity createTruckType(CreateTruckTypeRequest createTruckTypeRequest){
        try {
            if(createTruckTypeRequest == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "createTruckTypeRequest cannot be empty", createTruckTypeRequest));

             TruckType truckType = new TruckType();
             truckType.setName(createTruckTypeRequest.getTruckName());
             truckTypeRepo.save(truckType);
             return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Truck Type was created successfully", createTruckTypeRequest));
          }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
         }
    }

    public ResponseEntity editTruckType(EditTruckTypeRequest editTruckTypeRequest){
        try {
            if(editTruckTypeRequest == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "editTruckTypeRequest cannot be empty", editTruckTypeRequest));

            Optional<TruckType> truckType = truckTypeRepo.findById(editTruckTypeRequest.getTruckTypeId());
            if(!truckType.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid truckTypeId sent", editTruckTypeRequest));

            truckType.get().setName(editTruckTypeRequest.getTruckName());
            truckTypeRepo.save(truckType.get());
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Truck Type was updated successfully", editTruckTypeRequest));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
        }
    }


    public ResponseEntity getTruckTypeById(int truckTypeId){
        try {
            if(truckTypeId == 0)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "invalid truckTypeId sent", truckTypeId));

            Optional<TruckType> truckType = truckTypeRepo.findById(truckTypeId);
            if(!truckType.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid truckTypeId sent", truckTypeId));

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", truckType.get()));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", truckTypeId));
        }
    }

    public ResponseEntity getTruckTypes(){
        try {
            List<TruckType> truckTypes = truckTypeRepo.findAll();
            if(truckTypes.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), "No record found", null));

             return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", truckTypes));
          }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
        }
    }

    public ResponseEntity removeTruckTypeById(int truckTypeId) {
        try {
            if(truckTypeId == 0)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "invalid truckTypeId sent", truckTypeId));

            truckTypeRepo.deleteById(truckTypeId);
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Deletion Successful", truckTypeId));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", truckTypeId));
        }
    }
}
