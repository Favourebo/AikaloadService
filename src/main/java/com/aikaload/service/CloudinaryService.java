package com.aikaload.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class CloudinaryService {

    private static Cloudinary getConfig(){
      return new Cloudinary("cloudinary://123456789012345:abcdeghijklmnopqrstuvwxyz12@n07t21i7");
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        /** Map upload = getConfig().uploader().uploadLarge(file.getBytes(),
                                          ObjectUtils.asMap("resource_type", "video"));
        if(upload != null && upload.containsKey("url")){
                return (String) upload.get("url");
        }**/
        if(file.getSize() > 0) {
            return "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
        }
        return "";
    }
}
