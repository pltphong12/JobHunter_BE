package org.example.jobhunter.repository;

import org.example.jobhunter.domain.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByName(String name);
    Optional<Skill> findById(long id);
    Page<Skill> findAll(Specification<Skill> specification, Pageable pageable);
}
