package vn.minhdat.jobhunter_be.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.minhdat.jobhunter_be.dto.response.UploadFileResponse;
import vn.minhdat.jobhunter_be.exception.StorageException;
import vn.minhdat.jobhunter_be.service.FileService;
import vn.minhdat.jobhunter_be.util.annotation.ApiMessage;

import java.io.FileNotFoundException;
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

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder
    ) throws StorageException, URISyntaxException, FileNotFoundException {
        if(fileName == null || folder == null) {
            throw new StorageException("Missing required params : (fileName or folder) in query params");
        }

        String finalFolder = baseURI + folder;

        long fileSize = this.fileService.handleGetFileSize(fileName, finalFolder);
        if(fileSize <= 0) {
            throw new StorageException("File with name = " + fileName + " not found");
        }

        InputStreamResource resource = this.fileService.handleDownloadFile(fileName, finalFolder);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileSize)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
