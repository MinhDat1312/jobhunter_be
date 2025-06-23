package vn.minhdat.jobhunter_be.service;

import com.cloudinary.Cloudinary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.minhdat.jobhunter_be.dto.response.CloudinaryResponse;
import vn.minhdat.jobhunter_be.exception.InvalidException;

import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Transactional
    public CloudinaryResponse handleUploadFile(MultipartFile file, String folder, String fileName)
            throws InvalidException {
        try {
            Map<String, Object> result = this.cloudinary.uploader()
                    .upload(file.getBytes(),
                            Map.of("public_id",
                                    "jobhunter/" + folder + "/" + fileName));
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            return CloudinaryResponse.builder()
                    .publicId(publicId).url(url)
                    .build();

        } catch (Exception e) {
            throw new InvalidException("Failed to upload file");
        }
    }
}
