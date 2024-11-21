package org.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.example.jobhunter.domain.Job;
import org.example.jobhunter.domain.Skill;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.domain.response.job.ResJobDTO;
import org.example.jobhunter.service.JobService;
import org.example.jobhunter.service.SkillService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;
    private final SkillService skillService;
    private final ModelMapper modelMapper;

    public JobController(JobService jobService, SkillService skillService,ModelMapper modelMapper) {
        this.jobService = jobService;
        this.skillService = skillService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/jobs")
    @ApiMessage(value = "Create a job")
    public ResponseEntity<ResJobDTO> createJob(@Valid @RequestBody Job newJob) {
        ResJobDTO resJobDTO = new ResJobDTO();
        List<Skill> skills = newJob.getSkills();
        List<String> skillList = new ArrayList<>();
        for (Skill skill : skills) {
                Skill fetchASkill = this.skillService.handleFetchASkill(skill.getId());
                if (fetchASkill != null) {
                    skillList.add(fetchASkill.getName());
                }
        }
        Job job = this.jobService.handleCreateJob(newJob);
        resJobDTO = ResJobDTO.fromJob(job);
        resJobDTO.setSkills(skillList);

        return ResponseEntity.status(HttpStatus.CREATED).body(resJobDTO);
    }

    @PutMapping("/jobs")
    @ApiMessage(value = "Update a job")
    public ResponseEntity<ResJobDTO> updateJob(@Valid @RequestBody Job newJob) {
        ResJobDTO resJobDTO = new ResJobDTO();
        List<Skill> skills = newJob.getSkills();
        List<String> skillList = new ArrayList<>();
        for (Skill skill : skills) {
            Skill fetchASkill = this.skillService.handleFetchASkill(skill.getId());
            if (fetchASkill != null) {
                skillList.add(fetchASkill.getName());
            }
        }
        Job job = this.jobService.handleUpdateJob(newJob);
        resJobDTO = ResJobDTO.fromJob(job);
        resJobDTO.setSkills(skillList);

        return ResponseEntity.status(HttpStatus.CREATED).body(resJobDTO);
    }

    @GetMapping("/jobs")
    @ApiMessage(value = "Fetch All Job")
    public ResponseEntity<ResPaginationDTO> getAllJobs(
            @Filter Specification<Job> specification,
            Pageable pageable
            ) {
        ResPaginationDTO resPaginationDTO = this.jobService.handleFetchAllJob(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resPaginationDTO);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage(value = "Fetch a Job")
    public ResponseEntity<ResJobDTO> getJob(@PathVariable Long id) {
        Job job = this.jobService.handleFetchAJob(id);
        ResJobDTO resJobDTO = ResJobDTO.fromJob(job);

        List<Skill> skills = job.getSkills();
        List<String> skillList = new ArrayList<>();
        for (Skill skill : skills) {
            Skill fetchASkill = this.skillService.handleFetchASkill(skill.getId());
            if (fetchASkill != null) {
                skillList.add(fetchASkill.getName());
            }
        }
        resJobDTO.setSkills(skillList);
        return ResponseEntity.status(HttpStatus.OK).body(resJobDTO);
    }

    @DeleteMapping("jobs/{id}")
    @ApiMessage(value = "Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        this.jobService.handleDeleteAJob(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
