package com.erp.backend.service;

import com.erp.backend.dto.CreateQuizRequest;
import com.erp.backend.dto.SubmitQuizRequest;
import com.erp.backend.model.Attendance;
import com.erp.backend.model.Quiz;
import com.erp.backend.model.User;
import com.erp.backend.repository.AttendanceRepository;
import com.erp.backend.repository.QuizRepository;
import com.erp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public Quiz createQuiz(CreateQuizRequest request, String teacherEmail) {
        Quiz quiz = new Quiz();

        quiz.setTitle(request.getTitle());
        quiz.setTargetTag(request.getTargetTag());
        quiz.setQuestions(request.getQuestions());
        quiz.setCorrectAnswers(request.getCorrectAnswers());
        quiz.setCreatedByEmail(teacherEmail);

        return quizRepository.save(quiz);
    }

    public List<Quiz> getTeacherQuizzes(String teacherEmail) {
        return quizRepository.findByCreatedByEmail(teacherEmail);
    }

    public List<Quiz> getAvailableQuizzes() {
        return quizRepository.findAll();
    }

    public String submitQuiz(SubmitQuizRequest request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Error: Quiz not found!"));

        User student = userRepository.findByEmail(request.getStudentEmail())
                .orElseThrow(() -> new RuntimeException("Error: Student not found!"));

        List<String> correct = quiz.getCorrectAnswers();
        List<String> submitted = request.getSubmittedAnswers();
        int score = 0;

        for (int i = 0; i < Math.min(correct.size(), submitted.size()); i++) {
            if (correct.get(i).equalsIgnoreCase(submitted.get(i).trim())) {
                score++;
            }
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setQuizTitle(quiz.getTitle());
        attendance.setStatus("PRESENT");
        attendance.setTimestamp(LocalDateTime.now());
        attendanceRepository.save(attendance);

        return "Quiz submitted! Score: " + score + "/" + correct.size() + ". Attendance recorded as PRESENT!";
    }

    public List<Attendance> getStudentAttendance(String studentEmail) {
        return attendanceRepository.findByStudentEmail(studentEmail);
    }
}