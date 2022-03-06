package com.aikaload.service;

import com.aikaload.dto.GoogleDriveRequest;
import com.aikaload.dto.Response;
import com.aikaload.enums.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Log4j2
@Service("FileUploadService")
@AllArgsConstructor
public class FileUploadService {

    public ResponseEntity googleDriveUpload(GoogleDriveRequest googleDriveRequest) {
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", "http://upload-link"));
    }
}
