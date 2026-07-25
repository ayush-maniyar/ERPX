package com.erp.backend.repository;

import com.erp.backend.model.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {
    Optional<ClassGroup> findByTagName(String tagName);
    Boolean existsByTagName(String tagName);
}