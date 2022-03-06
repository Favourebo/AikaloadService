package com.aikaload.controller;

import com.aikaload.dto.GoogleDriveRequest;
import com.aikaload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file-upload")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileUploadController {
    private final FileUploadService fileUploadService;

    @PostMapping("/google-drive-upload")
    public ResponseEntity googleDriveUpload(@RequestBody GoogleDriveRequest googleDriveRequest){
        return fileUploadService.googleDriveUpload(googleDriveRequest);
    }
}
