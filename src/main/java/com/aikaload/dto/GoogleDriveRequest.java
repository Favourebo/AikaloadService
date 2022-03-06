package com.aikaload.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GoogleDriveRequest {
    private MultipartFile cacCertificateDoc;
    private MultipartFile shareAllocationDoc;
    private MultipartFile directorsDocumentDoc;
    private String userId;
}
