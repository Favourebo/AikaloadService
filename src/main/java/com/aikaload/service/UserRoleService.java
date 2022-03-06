package com.aikaload.service;

import com.aikaload.dto.Response;
import com.aikaload.entity.UserRole;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.repo.UserRoleRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class UserRoleService {
    private UserRoleRepo userRoleRepo;

    public ResponseEntity getRoles(){
        try{
            List<UserRole> userRoles = userRoleRepo.findAll();
            if(userRoles.isEmpty()){
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
            }
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),userRoles));
        }catch(Exception e){
            log.error("An error occurred while trying to get all roles::"+e.getMessage());
            return ResponseEntity.ok().body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(),ResponseEnum.AN_ERROR_OCCURRED.getMessage(),null));
        }
    }
}
