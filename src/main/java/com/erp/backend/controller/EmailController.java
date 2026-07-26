package com.erp.backend.controller;

import com.erp.backend.dto.SendTagEmailRequest;
import com.erp.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@PreAuthorize("hasRole('TEACHER')")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-by-tag")
    public ResponseEntity<String> sendEmailToGroup(@RequestBody SendTagEmailRequest request) {
        String response = emailService.sendEmailToGroup(request);
        return ResponseEntity.ok(response);
    }
}
