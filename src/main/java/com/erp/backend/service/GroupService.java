package com.erp.backend.service;

import com.erp.backend.dto.AddStudentToGroupRequest;
import com.erp.backend.dto.CreateGroupRequest;
import com.erp.backend.model.ClassGroup;
import com.erp.backend.model.Role;
import com.erp.backend.model.User;
import com.erp.backend.repository.ClassGroupRepository;
import com.erp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

    @Autowired
    private ClassGroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public String createGroup(CreateGroupRequest request) {
        if (groupRepository.existsByTagName(request.getTagName())) {
            throw new RuntimeException("Error: Tag name already exists!");
        }

        ClassGroup group = new ClassGroup();
        group.setTagName(request.getTagName());
        group.setDescription(request.getDescription());

        groupRepository.save(group);
        return "Class group created successfully!";
    }

    public String addStudentToGroup(AddStudentToGroupRequest request) {
        // 1. Find the group by tag name
        ClassGroup group = groupRepository.findByTagName(request.getTagName())
                .orElseThrow(() -> new RuntimeException("Error: Group tag not found!"));

        // 2. Find the student by email
        User student = userRepository.findByEmail(request.getStudentEmail())
                .orElseThrow(() -> new RuntimeException("Error: Student email not found!"));

        // 3. Verify user is actually a student
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Error: Selected user is not a student!");
        }

        // 4. Add student to group and save
        group.getStudents().add(student);
        groupRepository.save(group);

        return "Student added to group successfully!";
    }
}