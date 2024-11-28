package org.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.jobhunter.domain.Permission;
import org.example.jobhunter.domain.Role;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.service.PermissionService;
import org.example.jobhunter.service.RoleService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;
    private final PermissionService permissionService;

    public RoleController(RoleService roleService, PermissionService permissionService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role newRole) throws BadRequestException {
        if (this.roleService.existName(newRole.getName())) {
            throw new BadRequestException("Name is exist");
        }
        List<Permission> permissions = newRole.getPermissions();
        List<Permission> newPermissions = new ArrayList<Permission>();
        for(Permission permission : permissions) {
            Permission newPermission = this.permissionService.getPermissionById(permission.getId());
            if (newPermission != null) {
                newPermissions.add(newPermission);
            }
        }
        newRole.setPermissions(newPermissions);
        Role role = this.roleService.createRole(newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping("/roles")
    @ApiMessage("update a role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role newRole) throws BadRequestException {
        if (!this.roleService.existId(newRole.getId())) {
            throw new BadRequestException("Id is not exist");
        }
        if (this.roleService.existName(newRole.getName())) {
            throw new BadRequestException("Name is exist");
        }
        List<Permission> permissions = newRole.getPermissions();
        List<Permission> newPermissions = new ArrayList<Permission>();
        for(Permission permission : permissions) {
            Permission newPermission = this.permissionService.getPermissionById(permission.getId());
            if (newPermission != null) {
                newPermissions.add(newPermission);
            }
        }
        newRole.setPermissions(newPermissions);
        Role role = this.roleService.updateRole(newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @GetMapping("/roles")
    @ApiMessage("fetch all roles")
    public ResponseEntity<ResPaginationDTO> getAllRoles(
            @Filter Specification<Role> specification,
            Pageable pageable
    ) {
        ResPaginationDTO resPaginationDTO = this.roleService.handleFetchAllRoles(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resPaginationDTO);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable long id) throws BadRequestException {
        if (!this.roleService.existId(id)) {
            throw new BadRequestException("Id is not exist");
        }
        this.roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
