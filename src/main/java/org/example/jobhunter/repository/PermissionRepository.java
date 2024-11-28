package org.example.jobhunter.repository;

import org.example.jobhunter.domain.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    boolean existsByNameAndApiPathAndMethodAndModule(String name, String apiPath, String method, String module);
    boolean existsById(long id);
    Page<Permission> findAll(Specification<Permission> specification,Pageable pageable);
}
