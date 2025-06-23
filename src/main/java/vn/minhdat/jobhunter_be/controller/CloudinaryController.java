package vn.minhdat.jobhunter_be.controller;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.minhdat.jobhunter_be.dto.response.CloudinaryResponse;
import vn.minhdat.jobhunter_be.exception.InvalidException;
import vn.minhdat.jobhunter_be.exception.StorageException;
import vn.minhdat.jobhunter_be.service.CloudinaryService;
import vn.minhdat.jobhunter_be.util.FileUploadUtil;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1")
public class CloudinaryController {
    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/files")
    public ResponseEntity<CloudinaryResponse> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam(name = "folder") String folder
    ) throws StorageException, URISyntaxException, IOException, InvalidException {
        if(file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload file");
        }

        FileUploadUtil.assertAllowed(file, FileUploadUtil.FILE_PATTERN);

        String fileName = FileUploadUtil.getFileName(FilenameUtils.getBaseName(file.getOriginalFilename()));
        CloudinaryResponse response = this.cloudinaryService.handleUploadFile(file, folder, fileName);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @GetMapping("/files")
//    @ApiMessage("Download a file")
//    public ResponseEntity<Resource> downloadFile(
//            @RequestParam(name = "fileName", required = false) String fileName,
//            @RequestParam(name = "folder", required = false) String folder
//    ) throws StorageException, URISyntaxException, FileNotFoundException {
//        if(fileName == null || folder == null) {
//            throw new StorageException("Missing required params : (fileName or folder) in query params");
//        }
//
//        String finalFolder = baseURI + folder;
//
//        long fileSize = this.fileService.handleGetFileSize(fileName, finalFolder);
//        if(fileSize <= 0) {
//            throw new StorageException("File with name = " + fileName + " not found");
//        }
//
//        InputStreamResource resource = this.fileService.handleDownloadFile(fileName, finalFolder);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
//                .contentLength(fileSize)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
}
