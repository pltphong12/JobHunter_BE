package org.example.jobhunter.service;

import org.example.jobhunter.domain.Skill;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean handleExistSkill(Skill skill) {
        return this.skillRepository.existsByName(skill.getName());
    }

    public boolean handleExistSkillById(Skill skill) {
        return this.skillRepository.existsById(skill.getId());
    }

    public Skill handleUpdateSkill(Skill skill) {
        Skill currentSkill = this.skillRepository.findById(skill.getId()).get();
        if (currentSkill != null) {
            currentSkill.setName(skill.getName());
            return this.skillRepository.save(currentSkill);
        }
        return null;
    }

    public Skill handleFetchASkill(long id) {
        return this.skillRepository.findById(id).get();
    }

    public ResPaginationDTO handleFetchAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageSkill.getSize());

        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageSkill.getContent());

        return rs;
    }

    public void handleDeleteASkill(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        Skill currentSkill;
        if (skill.isPresent()) {
            currentSkill = skill.get();
        } else {
            currentSkill = null;
        }
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        this.skillRepository.deleteById(id);
    }
}
