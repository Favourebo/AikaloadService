package com.aikaload.service;

import com.aikaload.dto.*;
import com.aikaload.entity.LoadCategory;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.repo.LoadCategoryRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoadCategoryService {
    private final LoadCategoryRepo loadCategoryRepo;

    public ResponseEntity createLoadCategory(CreateLoadCategoryRequest createLoadCategoryRequest){
        try {
            if(createLoadCategoryRequest == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "createMaterialRequest cannot be empty", createLoadCategoryRequest));

            loadCategoryRepo.save(new LoadCategory(createLoadCategoryRequest.getMaterialName()));
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Material was created successfully", createLoadCategoryRequest));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
        }
    }

    public ResponseEntity editLoadCategory(EditLoadCategoryRequest editLoadCategoryRequest){
        try {
            if(editLoadCategoryRequest == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "editMaterialRequest cannot be empty", editLoadCategoryRequest));

            Optional<LoadCategory> loadCategory = loadCategoryRepo.findById(editLoadCategoryRequest.getMaterialId());
            if(!loadCategory.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid materialId sent", editLoadCategoryRequest));

            loadCategory.get().setName(editLoadCategoryRequest.getMaterialName());
            loadCategoryRepo.save(loadCategory.get());
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Material was updated successfully", editLoadCategoryRequest));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
        }
    }


    public ResponseEntity getLoadCategoryById(long loadCategoryId){
        try {
            if(loadCategoryId == 0)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "invalid materialId sent", loadCategoryId));

            Optional<LoadCategory> loadCategory = loadCategoryRepo.findById(loadCategoryId);
            if(!loadCategory.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid materialId sent", loadCategoryId));

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", loadCategory.get()));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", loadCategoryId));
        }
    }

    public ResponseEntity getLoadCategories(){
        try {
            List<LoadCategory> loadCategories = loadCategoryRepo.findAll();
            if(loadCategories.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), "No record found", null));

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", loadCategories));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", null));
        }
    }

    public ResponseEntity removeLoadCategoryById(long loadCategoryId) {
        try {
            if(loadCategoryId == 0)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "invalid materialId sent", loadCategoryId));

            loadCategoryRepo.deleteById(loadCategoryId);

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Deletion Successful", loadCategoryId));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.OPERATION_FAILED.getCode(), "An error occurred while trying to process request", loadCategoryId));
        }
    }
}
