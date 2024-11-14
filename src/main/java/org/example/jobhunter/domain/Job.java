package org.example.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.jobhunter.util.SecurityUtil;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String location;
    private double salary;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private LevelEnum level;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private Date startDate;
    private Date endDate;
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;



    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    public enum LevelEnum {
        INTERN, FRESHER, MIDDLE, SENIOR
    }

    @PrePersist
    public void handleBeforeCreate(){
        createdAt = Instant.now();
        createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
    }

    @PreUpdate
    public void handleBeforeUpdate(){
        updatedAt = Instant.now();
        updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
    }
}
