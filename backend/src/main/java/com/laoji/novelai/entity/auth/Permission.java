package com.laoji.novelai.entity.auth;

import com.laoji.novelai.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "permission")
@Data
@EqualsAndHashCode(callSuper = true)
public class Permission extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(length = 50)
    private String resourceType;

    @Column(length = 20)
    private String action;
}