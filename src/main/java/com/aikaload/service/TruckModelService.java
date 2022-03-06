package com.aikaload.service;

import com.aikaload.dto.*;
import com.aikaload.entity.TruckModel;
import com.aikaload.entity.TruckType;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.repo.TruckModelRepo;
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
public class TruckModelService {
    private final TruckModelRepo truckModelRepo;

    public ResponseEntity createTruckModel(CreateTruckModelRequest createTruckModelRequest){
        try {
            if(createTruckModelRequest == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "createTruckModelRequest cannot be empty", createTruckModelRequest));

             TruckModel truckModel = new TruckModel();
            truckModel.setName(createTruckModelRequest.getModelName());
            truckModelRepo.save(truckModel);
             return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Truck Model was created successfully", createTruckModelRequest));
          }catch(Exception e){
             e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
         }
    }

    public ResponseEntity editTruckModel(EditTruckModelRequest editTruckModelRequest){
        try {
            if(editTruckModelRequest == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "editTruckModelRequest cannot be empty", editTruckModelRequest));

            Optional<TruckModel> truckModel = truckModelRepo.findById(editTruckModelRequest.getModelId());
            if(!truckModel.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid modelId sent", editTruckModelRequest));

            truckModel.get().setName(editTruckModelRequest.getModelName());
            truckModelRepo.save(truckModel.get());
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Truck Model was updated successfully", editTruckModelRequest));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
        }
    }


    public ResponseEntity getTruckModelById(int modelId){
        try {
            if(modelId == 0)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "invalid modelId sent", modelId));

            Optional<TruckModel> truckModel = truckModelRepo.findById(modelId);
            if(!truckModel.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid modelId sent", modelId));

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", truckModel.get()));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", modelId));
        }
    }

    public ResponseEntity getTruckModels(){
        try {
            List<TruckModel> truckModels = truckModelRepo.findAll();
            if(truckModels.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), "No record found", null));

             return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", truckModels));
          }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
        }
    }

    public ResponseEntity removeTruckModelById(int truckModelId) {
        try {
            if(truckModelId == 0)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "invalid modelId sent", truckModelId));

            truckModelRepo.deleteById(truckModelId);
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Deletion Successful", truckModelId));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", truckModelId));
        }
    }
}
