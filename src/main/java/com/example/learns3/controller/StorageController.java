package com.example.learns3.controller;

import com.example.learns3.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) throws IOException {
        return  new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.OK);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> DownloadFile(@PathVariable String fileName) throws IOException {
        byte[] data = storageService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .contentLength(data.length)
                .header("Content-Type","application/octet-stream")
                .header("Content-disposition","attachment; filename=\""+fileName+"\"")
                .body(resource);
    }

    @GetMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName){
        return  new ResponseEntity<>(storageService.deleteObject(fileName), HttpStatus.OK);
    }

    @GetMapping("/presign/download/{fileName}")
    public ResponseEntity<String> presignDownloadUrl(@PathVariable String fileName){
        return  new ResponseEntity<>(storageService.generatePresignDownloadUrl(fileName), HttpStatus.OK);
    }

    @GetMapping("/presign/upload/{fileName}")
    public ResponseEntity<String> presignUploadUrl(@PathVariable String fileName){
        return  new ResponseEntity<>(storageService.generatePresignUploadUrl(fileName), HttpStatus.OK);
    }
}
