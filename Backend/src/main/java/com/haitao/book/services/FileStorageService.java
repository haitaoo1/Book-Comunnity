package com.haitao.book.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    //File path where we save the file
    public String saveFile(@NonNull MultipartFile sourceFile, @NonNull Integer userId) {

        //sub path for each user
        final String fileUploadSubPath = "user" + File.separator + userId;
        //sourceFile is the file we want to upload
        return uploadFile(sourceFile, fileUploadSubPath);

    }

    private String uploadFile(@NonNull MultipartFile sourceFile, @NonNull String fileUploadSubPath) {

        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        //will be ./uploads/users/userId/finalUploadPath

        File targetFolder = new File(finalUploadPath);
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated){
                log.warn("Failed to create the target folder");
                return null;
            }

        }
        //extract the file extension(pdf, jpg.....)
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        // ./upload/users/1/232334124.jpg
        String targetFilePath = finalUploadPath + File.separator + System.currentTimeMillis() + "." + fileExtension;

        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File save to " + targetFilePath);
            return targetFilePath;

        } catch (IOException e) {
            log.error("file was not save", e);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()){
            return "";
        }

        //something.jpg
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            //no extension file
            return "";
        }
        // .JPG -> .jpg
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
