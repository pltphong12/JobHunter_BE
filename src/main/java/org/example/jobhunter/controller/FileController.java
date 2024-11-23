package org.example.jobhunter.controller;

import org.example.jobhunter.domain.response.file.ResUploadFileDTO;
import org.example.jobhunter.exception.StogareException;
import org.example.jobhunter.service.FileService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Value("${jobhunter.upload-file.base-uri}")
    private String baseUri;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage(value = "Upload a file")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder
    ) throws URISyntaxException, StogareException {
        // file is empty
        if (file == null || file.isEmpty()) {
            throw new StogareException("File is empty");
        }
        // validate extension
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "png", "doc", "docx");
        boolean isValidExtension = allowedExtensions.stream().anyMatch(extension -> fileName.toLowerCase().endsWith(extension));
        if (!isValidExtension) {
            throw new StogareException("Invalid extension");
        }
        // create a directory
        this.fileService.createUploadFolder(baseUri + folder);
        // store a file
        String finalName = this.fileService.store(file, folder);
        ResUploadFileDTO uploadFileDTO = new ResUploadFileDTO(finalName, Instant.now());
        return ResponseEntity.ok().body(uploadFileDTO);
    }
}
