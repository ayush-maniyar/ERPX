package com.erp.backend.controller;

import com.erp.backend.dto.CreateVideoClassRequest;
import com.erp.backend.dto.MessageResponse;
import com.erp.backend.model.VideoClass;
import com.erp.backend.service.VideoClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class VideoClassController {

    @Autowired
    private VideoClassService videoClassService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MessageResponse> createVideoClass(@RequestBody CreateVideoClassRequest request) {
        String response = videoClassService.createVideoClass(request);
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @GetMapping("/tag/{tagName}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<VideoClass>> getClassesByTag(@PathVariable String tagName) {
        return ResponseEntity.ok(videoClassService.getClassesByTag(tagName));
    }
}
