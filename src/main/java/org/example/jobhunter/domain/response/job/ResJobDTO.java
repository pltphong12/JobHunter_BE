package org.example.jobhunter.domain.response.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.jobhunter.domain.Job;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResJobDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private Job.LevelEnum level;

    private List<String> skills;

    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;

    private String createdBy;

    public static ResJobDTO fromJob(Job job) {
        ResJobDTO resJobDTO = new ResJobDTO();
        resJobDTO.setId(job.getId());
        resJobDTO.setName(job.getName());
        resJobDTO.setLocation(job.getLocation());
        resJobDTO.setSalary(job.getSalary());
        resJobDTO.setQuantity(job.getQuantity());
        resJobDTO.setLevel(job.getLevel());
        resJobDTO.setStartDate(job.getStartDate().toInstant());
        resJobDTO.setEndDate(job.getEndDate().toInstant());
        resJobDTO.setActive(job.isActive());
        resJobDTO.setCreatedAt(job.getCreatedAt());
        resJobDTO.setCreatedBy(job.getCreatedBy());

        return resJobDTO;
    }
}
