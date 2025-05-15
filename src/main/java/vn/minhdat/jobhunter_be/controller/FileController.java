package vn.minhdat.jobhunter_be.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.minhdat.jobhunter_be.dto.response.UploadFileResponse;
import vn.minhdat.jobhunter_be.exception.StorageException;
import vn.minhdat.jobhunter_be.service.FileService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${minhdat.upload-file.base-uri}")
    private String baseURI;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public ResponseEntity<UploadFileResponse> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(name = "folder") String folder
    ) throws StorageException, URISyntaxException, IOException {
        if(file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload file");
        }

        List<String> allowedExtensions = Arrays.asList(".pdf", ".jpg", ".jpeg", ".png", ".doc", ".docx");
        boolean validated = allowedExtensions.stream()
                .anyMatch(item -> file.getOriginalFilename().toLowerCase().endsWith(item));
        if (!validated) {
            throw new StorageException("Invalid file extension, only allows: " + allowedExtensions.toString());
        }

        String finalFolder = baseURI + folder;
        this.fileService.handleCreateFolder(finalFolder);
        String finalFileName = this.fileService.handleStoreFile(file, finalFolder);

        UploadFileResponse uploadFileResponse = new UploadFileResponse(
                finalFileName, Instant.now()
        );

        return ResponseEntity.status(HttpStatus.OK).body(uploadFileResponse);
    }
}
