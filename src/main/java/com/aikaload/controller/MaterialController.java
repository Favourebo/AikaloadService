package com.aikaload.controller;

import com.aikaload.asyncservice.AuditTrailService;
import com.aikaload.dto.CreateLoadCategoryRequest;
import com.aikaload.dto.EditLoadCategoryRequest;
import com.aikaload.entity.AuditTrail;
import com.aikaload.service.LoadCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@RestController
@RequestMapping("/material")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MaterialController {
    private final LoadCategoryService loadCategoryService;
    private final AuditTrailService auditTrailService;

    /**
     * This method creates material
     * @param createLoadCategoryRequest contains material information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/create-material")
    public ResponseEntity createMaterial(@RequestBody CreateLoadCategoryRequest createLoadCategoryRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"CREATE_MATERIAL",createLoadCategoryRequest.toString(),new Date()));
        return loadCategoryService.createLoadCategory(createLoadCategoryRequest);
    }

    /**
     * This method edits material
     * @param editLoadCategoryRequest contains material information
     * @return  ResponseEntity contains response
     */
    @PostMapping("/edit-material")
    public ResponseEntity editMaterial(@RequestBody EditLoadCategoryRequest editLoadCategoryRequest, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"EDIT_MATERIAL",editLoadCategoryRequest.toString(),new Date()));
        return loadCategoryService.editLoadCategory(editLoadCategoryRequest);
    }


    @GetMapping("/get-material-by-id/{materialId}")
    public ResponseEntity getMaterialById(@PathVariable long materiaId){
        return loadCategoryService.getLoadCategoryById(materiaId);
    }


    @GetMapping("/remove-material-by-id/{materialId}")
    public ResponseEntity removeMaterialById(@PathVariable long materialId, Authentication authentication){
        auditTrailService.saveAudit(new AuditTrail(authentication.getName(),"REMOVE_MATERIAL_BY_ID",String.valueOf(materialId),new Date()));
        return loadCategoryService.removeLoadCategoryById(materialId);
    }


    @GetMapping("/get-materials")
    public ResponseEntity getMaterials(){
        return loadCategoryService.getLoadCategories();
    }
}
