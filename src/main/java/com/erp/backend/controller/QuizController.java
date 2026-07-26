package com.erp.backend.controller;

import com.erp.backend.dto.CreateQuizRequest;
import com.erp.backend.dto.SubmitQuizRequest;
import com.erp.backend.model.Attendance;
import com.erp.backend.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/create")
    public ResponseEntity<String> createQuiz(@RequestBody CreateQuizRequest request) {
        try {
            String response = quizService.createQuiz(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitQuiz(@RequestBody SubmitQuizRequest request) {
        try {
            String response = quizService.submitQuiz(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/attendance/{email}")
    public ResponseEntity<?> getAttendance(@PathVariable String email) {
        try {
            List<Attendance> attendanceList = quizService.getStudentAttendance(email);
            return ResponseEntity.ok(attendanceList);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}