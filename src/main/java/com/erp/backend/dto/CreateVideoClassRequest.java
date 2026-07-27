package com.erp.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateVideoClassRequest {
    private String title;
    private String targetTag;
    private String meetLink;
    private LocalDateTime scheduledTime;
}
