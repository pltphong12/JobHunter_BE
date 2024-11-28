package org.example.jobhunter.service;

import org.example.jobhunter.domain.Permission;
import org.example.jobhunter.domain.Skill;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissionService {
    private PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermission(Permission permission) {
        return this.permissionRepository.existsById(permission.getId());
    }

    public Permission getPermissionById(long id) {
        return this.permissionRepository.findById(id).orElse(null);
    }

    public boolean isPermissionExist(Permission permission) {
        return (this.permissionRepository.existsByNameAndApiPathAndMethodAndModule(permission.getName(), permission.getApiPath(), permission.getMethod(), permission.getModule()));
    }

    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission updatePermission(Permission permission) {
        Permission currentPermission = this.permissionRepository.findById(permission.getId()).orElse(null);
        if (currentPermission != null) {
            currentPermission.setName(permission.getName());
            currentPermission.setApiPath(permission.getApiPath());
            currentPermission.setMethod(permission.getMethod());
            currentPermission.setModule(permission.getModule());

            return permissionRepository.save(currentPermission);
        }
        return null;
    }

    public ResPaginationDTO handleFetchAllPermission(Specification<Permission> spec, Pageable pageable) {
        ResPaginationDTO rs = new ResPaginationDTO();
        Page<Permission> pagePermission = this.permissionRepository.findAll(spec, pageable);
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pagePermission.getSize());

        mt.setPages(pagePermission.getTotalPages());
        mt.setTotal(pagePermission.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pagePermission.getContent());
        return rs;
    }

    public void deletePermission(long id) {
        Optional<Permission> permission = this.permissionRepository.findById(id);
        Permission currentPermission;
        if (permission.isPresent()) {
            currentPermission = permission.get();
        } else {
            currentPermission = null;
        }
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));
        this.permissionRepository.deleteById(id);
    }
}
