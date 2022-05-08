package com.aikaload.controller;

import com.aikaload.dto.GoogleDriveRequest;
import com.aikaload.service.CloudinaryService;
import com.aikaload.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class FileUploadController {
    private final FileUploadService fileUploadService;
    private final CloudinaryService cloudinaryService;

    @PostMapping("/google-drive-upload")
    public ResponseEntity googleDriveUpload(@RequestBody GoogleDriveRequest googleDriveRequest){
        return fileUploadService.googleDriveUpload(googleDriveRequest);
    }

    @PostMapping(value="/get-link",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> getUrl(@RequestParam("file") MultipartFile  file) throws IOException {
        return ResponseEntity.ok().body(cloudinaryService.uploadVideo(file));
    }

}
