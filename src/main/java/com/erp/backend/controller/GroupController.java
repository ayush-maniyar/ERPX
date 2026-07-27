package com.erp.backend.controller;

import com.erp.backend.dto.AddStudentToGroupRequest;
import com.erp.backend.dto.CreateGroupRequest;
import com.erp.backend.dto.MessageResponse;
import com.erp.backend.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasRole('TEACHER')")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping("/create")
    public ResponseEntity<MessageResponse> createGroup(@RequestBody CreateGroupRequest request) {
        String response = groupService.createGroup(request);
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @PostMapping("/add-student")
    public ResponseEntity<MessageResponse> addStudentToGroup(@RequestBody AddStudentToGroupRequest request) {
        String response = groupService.addStudentToGroup(request);
        return ResponseEntity.ok(new MessageResponse(response));
    }
}
