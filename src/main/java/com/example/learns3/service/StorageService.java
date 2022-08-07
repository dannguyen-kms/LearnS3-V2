package com.example.learns3.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.*;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class StorageService {
    @Value("${application.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        S3Client client = S3Client.builder().build();
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName)
                .key(fileName)
                .build();

        client.putObject(request, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getInputStream().available()));
        return "file uploaded: "+fileName;
    }

    public byte[] downloadFile(String fileName) throws IOException {
        S3Client client = S3Client.builder().build();
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName)
                .key(fileName)
                .build();

        ResponseBytes<GetObjectResponse> response = client.getObject(request, ResponseTransformer.toBytes());
        return response.asByteArray();
    }


    public String deleteObject(String fileName){
        S3Client client = S3Client.builder().build();
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        client.deleteObject(request);
        return "file deleted: "+fileName;
    }

    public String generatePresignDownloadUrl(String fileName){
        S3Presigner presigner = S3Presigner.create();
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest getObjectPresignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(request)
                        .build();

        PresignedGetObjectRequest presignedGetObjectRequest =
                presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    public String generatePresignUploadUrl(String fileName){
        S3Presigner presigner = S3Presigner.create();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        PutObjectPresignRequest putObjectPresignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(request)
                        .build();

        PresignedPutObjectRequest presignedPutObjectRequest =
                presigner.presignPutObject(putObjectPresignRequest);
        return presignedPutObjectRequest.url().toString();
    }
}
