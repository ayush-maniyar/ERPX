package com.erp.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String targetTag;

    @Column(nullable = false)
    private String meetLink;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;
}
