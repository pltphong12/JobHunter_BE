package org.example.jobhunter.service;

import org.example.jobhunter.domain.Resume;
import org.example.jobhunter.domain.Skill;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.domain.response.resume.ResGetResumeDTO;
import org.example.jobhunter.repository.ResumeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ModelMapper modelMapper;

    public ResumeService(ResumeRepository resumeRepository, ModelMapper modelMapper) {
        this.resumeRepository = resumeRepository;
        this.modelMapper = modelMapper;
    }

    public Resume fetchResumeById(long id) {
        return this.resumeRepository.findById(id);
    }

    public Resume createResume(Resume resume) {
        return this.resumeRepository.save(resume);
    }

    public Resume updateResume(Resume resume) {
        Resume updatedResume = this.resumeRepository.findById(resume.getId());
        if (resume.getStatus() != null) {
            updatedResume.setStatus(resume.getStatus());
        }
        return this.resumeRepository.save(updatedResume);
    }

    public ResPaginationDTO fetchAllWithPagination(Specification<Resume> spec, Pageable pageable) {
        ResPaginationDTO rs = new ResPaginationDTO();
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();
        List<ResGetResumeDTO> page = new ArrayList<>();
        for (Resume resume : pageResume.getContent()) {
            ResGetResumeDTO.ResJobInResumeDTO job = new ResGetResumeDTO.ResJobInResumeDTO(resume.getJob().getId(), resume.getJob().getName());
            ResGetResumeDTO.ResUserInResumeDTO user = new ResGetResumeDTO.ResUserInResumeDTO(resume.getUser().getId(), resume.getUser().getName());
            ResGetResumeDTO resGetResumeDTO = modelMapper.map(resume, ResGetResumeDTO.class);
            resGetResumeDTO.setJob(job);
            resGetResumeDTO.setUser(user);
            page.add(resGetResumeDTO);
        }

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageResume.getSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(page);
        return rs;
    }

    public void deleteResumeById(long id) {
        this.resumeRepository.deleteById(id);
    }
}
