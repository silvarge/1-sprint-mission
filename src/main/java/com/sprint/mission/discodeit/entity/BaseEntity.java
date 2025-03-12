package com.sprint.mission.discodeit.entity;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;
}
