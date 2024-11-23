package org.example.jobhunter.repository;

import org.example.jobhunter.domain.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Resume findById(long id);
    Page<Resume> findAll(Specification<Resume> specification, Pageable pageable);
}
