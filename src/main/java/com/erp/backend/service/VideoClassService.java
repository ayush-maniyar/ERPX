package com.erp.backend.service;

import com.erp.backend.dto.CreateVideoClassRequest;
import com.erp.backend.model.VideoClass;
import com.erp.backend.repository.ClassGroupRepository;
import com.erp.backend.repository.VideoClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoClassService {

    @Autowired
    private VideoClassRepository videoClassRepository;

    @Autowired
    private ClassGroupRepository groupRepository;

    public String createVideoClass(CreateVideoClassRequest request) {
        if (!groupRepository.existsByTagName(request.getTargetTag())) {
            throw new RuntimeException("Error: Group tag not found!");
        }

        VideoClass videoClass = new VideoClass();
        videoClass.setTitle(request.getTitle());
        videoClass.setTargetTag(request.getTargetTag());
        videoClass.setMeetLink(request.getMeetLink());
        videoClass.setScheduledTime(request.getScheduledTime());

        videoClassRepository.save(videoClass);
        return "Video class scheduled successfully!";
    }

    public List<VideoClass> getClassesByTag(String targetTag) {
        return videoClassRepository.findByTargetTagOrderByScheduledTimeAsc(targetTag);
    }
}
