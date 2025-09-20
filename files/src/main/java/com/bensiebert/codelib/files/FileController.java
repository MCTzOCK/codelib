package com.bensiebert.codelib.files;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RestController
public class FileController {

    @Autowired
    public MinIOStorageService storageService;

    @Operation(summary = "Upload a file", tags = {"Files"})
    @ApiResponses(value = {})
    @RequestMapping(path = "/files/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("File is empty"));
        }
        String objectName = storageService.uploadFile(file);
        return ResponseEntity.ok().body(new UploadResponse(objectName));
    }

    @Operation(summary = "Redirect to file", tags = {"Files"})
    @ApiResponses(value = {})
    @RequestMapping(path = "/files/{objectName}", method = RequestMethod.GET)
    public ResponseEntity<Void> getFile(@PathVariable String objectName) {
        Duration expiry = Duration.ofSeconds(500);
        String presignedUrl = storageService.getPresignedUrl(objectName, expiry);
        return ResponseEntity.status(302)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ErrorResponse {
        public String error;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class UploadResponse {
        public String objectName;
    }
}
