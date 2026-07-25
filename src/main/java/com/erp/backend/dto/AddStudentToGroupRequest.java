package com.erp.backend.dto;

import lombok.Data;

@Data
public class AddStudentToGroupRequest {
    private String tagName;
    private String studentEmail;
}