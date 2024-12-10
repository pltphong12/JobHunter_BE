package org.example.jobhunter.service;

import org.example.jobhunter.domain.Company;
import org.example.jobhunter.domain.Job;
import org.example.jobhunter.domain.Skill;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.repository.CompanyRepository;
import org.example.jobhunter.repository.JobRepository;
import org.example.jobhunter.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Job handleCreateJob(Job job) {
        List<Skill> skills = job.getSkills();
        for (Skill skill : skills) {
            Optional<Skill> s = this.skillRepository.findById(skill.getId());
            if (!s.isPresent()) {
                skills.remove(skill);
            }
        }
        Company company = this.companyRepository.findById(job.getCompany().getId()).orElse(null);
        if (company == null) {
            job.setCompany(new Company());
        }else {
            job.setCompany(company);
        }
        job.setSkills(skills);
        return this.jobRepository.save(job);
    }

    public Job handleUpdateJob(Job job) {
        Optional<Job> currentJob = this.jobRepository.findById(job.getId());
        if (currentJob.isPresent()) {
            if (job.getName() != null){
                currentJob.get().setName(job.getName());
            }
            if (job.getLocation() != null){
                currentJob.get().setLocation(job.getLocation());
            }
            if (job.getSalary() != 0){
                currentJob.get().setSalary(job.getSalary());
            }
            if (job.getQuantity() != 0){
                currentJob.get().setQuantity(job.getQuantity());
            }
            if (job.getLevel() != null){
                currentJob.get().setLevel(job.getLevel());
            }
            if (job.getDescription() != null){
                currentJob.get().setDescription(job.getDescription());
            }
            if (job.getStartDate() != null){
                currentJob.get().setStartDate(job.getStartDate());
            }
            if (job.getEndDate() != null){
                currentJob.get().setEndDate(job.getEndDate());
            }
            job.setActive(job.isActive());
            if (job.getCompany() != null){
                currentJob.get().setCompany(job.getCompany());
            }
            if (job.getSkills() != null){
                List<Skill> skillList = job.getSkills();
                for (Skill skill : skillList) {
                    Optional<Skill> s = this.skillRepository.findById(skill.getId());
                    if (!s.isPresent()) {
                        skillList.remove(skill);
                    }
                }
                job.setSkills(skillList);
            }
            return this.jobRepository.save(currentJob.get());
        }
        return null;
    }

    public ResPaginationDTO handleFetchAllJob(Specification<Job> spec, Pageable pageable) {
        ResPaginationDTO rs = new ResPaginationDTO();
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageJob.getSize());

        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageJob.getContent());
        return rs;
    }

    public Job handleFetchAJob(long id){
        Optional<Job> currentJob = this.jobRepository.findById(id);
        return currentJob.get();
    }

    public void handleDeleteAJob(long id){
        this.jobRepository.deleteById(id);
    }
}
