package com.gst.demo.invoiceservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${invoice.upload.dir:C:/data/invoices}")
    private String invoicesDir;

    private final RestTemplate restTemplate;

    public InvoiceController(KafkaTemplate<String, String> kafkaTemplate, RestTemplate restTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                   @RequestPart("file") MultipartFile file) throws IOException {
        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication token is missing or invalid"));
        }

        // Validate the token by calling auth-service
        String token = authHeader.substring(7);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange("http://auth-service/api/auth/validate", HttpMethod.GET, entity, Map.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || !(Boolean) response.getBody().get("valid")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid authentication token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token validation failed: " + e.getMessage()));
        }

        Path dirPath = Paths.get(invoicesDir);

        // Ensure directory exists
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = dirPath.resolve(filename);

        // Save file
        file.transferTo(filePath.toFile());

        // Send filename to Kafka
        kafkaTemplate.send("invoices", filename);

        return ResponseEntity.ok(Map.of(
                "status", "uploaded",
                "filename", filename,
                "path", filePath.toAbsolutePath().toString()
        ));
    }
}
