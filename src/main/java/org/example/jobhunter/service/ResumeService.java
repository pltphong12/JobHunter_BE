package org.example.jobhunter.service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.example.jobhunter.domain.Resume;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.domain.response.resume.ResGetResumeDTO;
import org.example.jobhunter.repository.ResumeRepository;
import org.example.jobhunter.util.SecurityUtil;
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
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, ModelMapper modelMapper, FilterParser filterParser, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeRepository = resumeRepository;
        this.modelMapper = modelMapper;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
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
            resGetResumeDTO.setCompanyName(resume.getJob().getCompany().getName());
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

    public ResPaginationDTO fetchResumeByUser(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : " ";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        ResPaginationDTO rs = new ResPaginationDTO();
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageResume.getSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageResume.getContent());
        return rs;
    }
}
