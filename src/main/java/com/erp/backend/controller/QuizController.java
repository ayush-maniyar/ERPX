package com.erp.backend.controller;

import com.erp.backend.dto.CreateQuizRequest;
import com.erp.backend.dto.MessageResponse;
import com.erp.backend.dto.SubmitQuizRequest;
import com.erp.backend.model.Attendance;
import com.erp.backend.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<MessageResponse> createQuiz(@RequestBody CreateQuizRequest request) {
        String response = quizService.createQuiz(request);
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<MessageResponse> submitQuiz(@RequestBody SubmitQuizRequest request) {
        String response = quizService.submitQuiz(request);
        return ResponseEntity.ok(new MessageResponse(response));
    }

    @GetMapping("/attendance/{email}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<List<Attendance>> getAttendance(@PathVariable String email) {
        List<Attendance> attendanceList = quizService.getStudentAttendance(email);
        return ResponseEntity.ok(attendanceList);
    }
}
