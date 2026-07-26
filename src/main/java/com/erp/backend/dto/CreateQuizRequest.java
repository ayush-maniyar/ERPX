package com.erp.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateQuizRequest {
    private String title;
    private String targetTag;
    private List<String> questions;
    private List<String> correctAnswers;
}