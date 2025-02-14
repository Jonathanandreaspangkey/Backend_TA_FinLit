package com.skripsi.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resources")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // Title of the resource
    private String type; // Type of resource (e.g., "article", "video", "link")
    private String url; // URL or link to the resource

    @ManyToOne
    @JoinColumn(name = "submodule_id", nullable = false)
    @JsonBackReference
    private SubModule subModule; // Relationship to SubModule
}