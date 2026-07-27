package com.erp.backend.repository;

import com.erp.backend.model.VideoClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoClassRepository extends JpaRepository<VideoClass, Long> {
    List<VideoClass> findByTargetTagOrderByScheduledTimeAsc(String targetTag);
}
