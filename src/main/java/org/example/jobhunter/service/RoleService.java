package org.example.jobhunter.service;

import org.example.jobhunter.domain.Role;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public boolean existName(String name){
        return this.roleRepository.existsByName(name);
    }

    public boolean existId(long id){
        return this.roleRepository.existsById(id);
    }

    public Role createRole(Role role){
        return this.roleRepository.save(role);
    }

    public Role updateRole(Role role){
        Optional<Role> currentRole = this.roleRepository.findById(role.getId());
        if(currentRole.isPresent()){
            currentRole.get().setName(role.getName());
            currentRole.get().setDescription(role.getDescription());
            currentRole.get().setPermissions(role.getPermissions());
            return this.roleRepository.save(currentRole.get());
        }
        return null;
    }

    public ResPaginationDTO handleFetchAllRoles(Specification<Role> spec, Pageable pageable) {
        ResPaginationDTO rs = new ResPaginationDTO();
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageRole.getSize());

        mt.setPages(pageRole.getTotalPages());
        mt.setTotal(pageRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageRole.getContent());
        return rs;
    }

    public void deleteRole(long id){
        this.roleRepository.deleteById(id);
    }

    public Role fetchRoleById(long id){
        Optional<Role> role = this.roleRepository.findById(id);
        if(role.isPresent()){
            return role.get();
        }
        return null;
    }
}
