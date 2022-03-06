package com.aikaload.controller;

import com.aikaload.service.UserRoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-role")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserRoleController {

    private final UserRoleService userRoleService;

    /**
     * This method helps to get all roles
     * @return ResponseEntity contains response information
     */
    @GetMapping(value = "/get-roles")
    public ResponseEntity getRoles(){
        return userRoleService.getRoles();
    }

}
