package com.tnp.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tnp.service.GpgService;

@RestController
@RequestMapping("/api/gpg")
public class GpgController {

    private final GpgService gpgService;

    public GpgController(GpgService gpgService) {
        this.gpgService = gpgService;
    }

    @PostMapping("/encrypt/{configId}")
    public ResponseEntity<String> encryptFile(@PathVariable Long configId) {
        try {
            gpgService.encryptFileFromDatabase(configId);
            return ResponseEntity.ok("File encrypted successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error encrypting file: " + e.getMessage());
        }
    }
}
