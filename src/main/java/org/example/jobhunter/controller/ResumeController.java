package org.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import org.apache.coyote.BadRequestException;
import org.example.jobhunter.domain.Company;
import org.example.jobhunter.domain.Job;
import org.example.jobhunter.domain.Resume;
import org.example.jobhunter.domain.User;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.domain.response.resume.ResCreateResumeDTO;
import org.example.jobhunter.domain.response.resume.ResGetResumeDTO;
import org.example.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import org.example.jobhunter.exception.StogareException;
import org.example.jobhunter.service.JobService;
import org.example.jobhunter.service.ResumeService;
import org.example.jobhunter.service.UserService;
import org.example.jobhunter.util.SecurityUtil;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final JobService jobService;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, JobService jobService, UserService userService, ModelMapper modelMapper, FilterBuilder filterBuilder, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.jobService = jobService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage(value = "Create a resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@RequestBody Resume resume) throws BadRequestException {
        Job job = this.jobService.handleFetchAJob(resume.getJob().getId());
        User user = this.userService.handleFetchUserById(resume.getUser().getId());
        if (job == null || user == null) {
            throw new BadRequestException("Job or user not found");
        }
        resume.setJob(job);
        resume.setUser(user);
        Resume newResume = this.resumeService.createResume(resume);
        ResCreateResumeDTO resCreateResumeDTO = modelMapper.map(newResume, ResCreateResumeDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateResumeDTO);
    }

    @PutMapping("/resumes")
    @ApiMessage(value = "Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws BadRequestException {
        if (this.resumeService.fetchResumeById(resume.getId()) == null) {
            throw new BadRequestException("Resume not found");
        }
        Resume currentResume = this.resumeService.updateResume(resume);
        ResUpdateResumeDTO resUpdateResumeDTO = modelMapper.map(currentResume, ResUpdateResumeDTO.class);
        return ResponseEntity.ok().body(resUpdateResumeDTO);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage(value = "fetch a resume by id")
    public ResponseEntity<ResGetResumeDTO> getResumeById(@PathVariable Long id) throws BadRequestException {
        Resume resume = this.resumeService.fetchResumeById(id);
        if (resume == null) {
            throw new BadRequestException("Resume not found");
        }
        ResGetResumeDTO resGetResumeDTO = modelMapper.map(this.resumeService.fetchResumeById(id), ResGetResumeDTO.class);
        ResGetResumeDTO.ResJobInResumeDTO job = new ResGetResumeDTO.ResJobInResumeDTO(resume.getJob().getId(), resume.getJob().getName());
        ResGetResumeDTO.ResUserInResumeDTO user = new ResGetResumeDTO.ResUserInResumeDTO(resume.getUser().getId(), resume.getUser().getName());
        resGetResumeDTO.setJob(job);
        resGetResumeDTO.setUser(user);
        resGetResumeDTO.setCompanyName(resume.getJob().getCompany().getName());
        return ResponseEntity.ok().body(resGetResumeDTO);
    }

    @GetMapping("/resumes")
    @ApiMessage(value = "fetch all resumes")
    public ResponseEntity<ResPaginationDTO> getAllResume(
            @Filter Specification<Resume> spec,
            Pageable pageable
    ){
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleFetchUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.fetchAllWithPagination(finalSpec, pageable));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage(value = "delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") Long id) throws BadRequestException {
        Resume resume = this.resumeService.fetchResumeById(id);
        if (resume == null) {
            throw new BadRequestException("Resume not found");
        }
        this.resumeService.deleteResumeById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("fetch resume by user")
    public ResponseEntity<ResPaginationDTO> fetchResumeByUser(Pageable pageable) throws BadRequestException {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
