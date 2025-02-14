package com.skripsi.api.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class SubModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    @JsonBackReference
    private CourseModule module;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "subModule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Material> materials;

    @OneToMany(mappedBy = "subModule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Quiz> quizzes;

    @Column(name = "order_number")
    private Integer orderNumber;

    @OneToMany(mappedBy = "subModule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<LearningObjective> learningObjectives;

    @Setter
    @Column(name = "quiz_completed")
    private Boolean quizCompleted = false;

    // Method to set completed materials count
    @Setter
    @Transient
    private Integer completedMaterialsCount;

    @OneToMany(mappedBy = "subModule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Resource> resources; // List of resources for this submodule

}


