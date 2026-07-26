package com.erp.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmitQuizRequest {
    private Long quizId;
    private String studentEmail;
    private List<String> submittedAnswers;
}