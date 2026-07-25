package com.erp.backend.controller;

import com.erp.backend.dto.SendTagEmailRequest;
import com.erp.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-by-tag")
    public ResponseEntity<String> sendEmailToGroup(@RequestBody SendTagEmailRequest request) {
        try {
            String response = emailService.sendEmailToGroup(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}