package com.erp.backend.dto;

import lombok.Data;

@Data
public class CreateGroupRequest {
    private String tagName;
    private String description;
}