package com.ssafy.htrip.plan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "member_roles")
public class MemberRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleNo;
    private String roleName;
    private Boolean canEdit;
    private Boolean canDelete;
    @Column(columnDefinition = "TEXT")
    private String description;
}
