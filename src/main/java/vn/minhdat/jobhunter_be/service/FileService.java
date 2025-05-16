package vn.minhdat.jobhunter_be.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    public void handleCreateFolder(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File file = new File(path.toString());

        if(!file.isDirectory()){
            try {
                Files.createDirectory(file.toPath());
                System.out.println("Directory created " + file.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            System.out.println("Skip making folder, already exists");
        }
    }

    public String handleStoreFile(MultipartFile fileName, String folder) throws URISyntaxException, IOException {
        String finalFileName = System.currentTimeMillis() + "-" + fileName.getOriginalFilename();
        URI uri = new URI(folder + "/" + finalFileName);
        Path path = Paths.get(uri);

        try(InputStream inputStream = fileName.getInputStream()){
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return finalFileName;
    }

    public long handleGetFileSize(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());

        if(file.isDirectory() || !file.exists()){
            return 0;
        }
        return file.length();
    }

    public InputStreamResource handleDownloadFile(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException
    {
        URI uri = new URI(folder + "/" + fileName);
        Path path = Paths.get(uri);
        File file = new File(path.toString());

        return new InputStreamResource(new FileInputStream(file));
    }
}
