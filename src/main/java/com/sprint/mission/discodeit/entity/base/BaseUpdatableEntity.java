package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter(value = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    private Instant updatedAt;
}
