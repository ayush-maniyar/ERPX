package com.erp.backend.service;

import com.erp.backend.dto.SendTagEmailRequest;
import com.erp.backend.model.ClassGroup;
import com.erp.backend.model.User;
import com.erp.backend.repository.ClassGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class EmailService {

    @Autowired
    private ClassGroupRepository groupRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public String sendEmailToGroup(SendTagEmailRequest request) {
        // 1. Retrieve group by tag name
        ClassGroup group = groupRepository.findByTagName(request.getTagName())
                .orElseThrow(() -> new RuntimeException("Error: Group tag not found!"));

        Set<User> students = group.getStudents();

        if (students.isEmpty()) {
            return "No students registered under tag: " + request.getTagName();
        }

        // 2. Extract student email addresses
        int emailCount = 0;
        for (User student : students) {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(student.getEmail());
                message.setSubject(request.getSubject());
                message.setText(request.getBody());
                mailSender.send(message);
            }
            emailCount++;
        }

        return "Successfully queued " + emailCount + " emails for group: " + request.getTagName();
    }
}