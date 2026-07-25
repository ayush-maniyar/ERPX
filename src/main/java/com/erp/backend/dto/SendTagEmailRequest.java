package com.erp.backend.dto;

import lombok.Data;

@Data
public class SendTagEmailRequest {
    private String tagName;
    private String subject;
    private String body;
}