package com.aikaload.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService {
    private final long MAX_FILE_SIZE_MB = 100;

    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", "aikaloadsnew",
        "api_key", "425159166857388",
        "api_secret", "JvrV-HrJTTlkDqrOl1EEV_WUBWI",
        "secure", true));


    public CloudinaryResponse uploadMedia(MultipartFile multipartFile){
        Map res;

        try {
            File file = new File(System.getProperty("java.io.tmpdir").concat("/").concat(multipartFile.getOriginalFilename()));
            multipartFile.transferTo(file);

            if(isFileLimitExceeded(file, MAX_FILE_SIZE_MB))
                return new CloudinaryResponse().setCode("555").setMessage("Maximum file size(100 mb) limit exceeded");
            String fileExtension = getFileExtension(file.getName()).toLowerCase(Locale.ROOT);
            switch (fileExtension) {
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                    res = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                    break;
                case "mp4":
                    res = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                        "resource_type", "video"
                                                                              ));
                    break;
                default:
                    return new CloudinaryResponse().setCode("999").setMessage("Invalid file type provided");
            }

            return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).convertValue(res, CloudinaryResponse.class).setCode("000").setMessage("Success");
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return new CloudinaryResponse().setCode("999").setMessage(e.getMessage());
        }
    }


    private String getFileExtension(String fileName){

        if(fileName == null || fileName.lastIndexOf(".") == -1)
            return "";

        return fileName.substring(fileName.lastIndexOf(".")+1);
    }


    private boolean isFileLimitExceeded(File file, long sizeLimitMB){

        return Math.ceil((file.length() / Math.pow(1000.0, 2))) > sizeLimitMB;
    }



    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CloudinaryResponse{

        private String code;
        private String message;
        private String public_id;
        private Long version;
        private String signature;
        private Long width;
        private Long height;
        private String format;
        private String resource_type;
        private String created_at;
        private Long bytes;
        private String type;
        private String url;
        private String secure_url;
        private String etag;
    }
}
