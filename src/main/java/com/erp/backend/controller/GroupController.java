package com.erp.backend.controller;

import com.erp.backend.dto.AddStudentToGroupRequest;
import com.erp.backend.dto.CreateGroupRequest;
import com.erp.backend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<String> createGroup(@RequestBody CreateGroupRequest request) {
        try {
            String response = groupService.createGroup(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add-student")
    public ResponseEntity<String> addStudentToGroup(@RequestBody AddStudentToGroupRequest request) {
        try {
            String response = groupService.addStudentToGroup(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}