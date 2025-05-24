package com.ssafy.htrip.attraction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "content_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttractionContentType {
    @Id
    private Integer contentTypeId;
    private String contentName;
}