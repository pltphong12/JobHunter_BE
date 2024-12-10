package org.example.jobhunter.repository;

import org.example.jobhunter.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String name);
    Page<Role> findAll(Specification<Role> specification, Pageable pageable);

    Role findByName(String superAdmin);
}
