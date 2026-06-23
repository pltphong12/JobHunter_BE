package org.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.jobhunter.domain.Role;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.service.RoleService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role newRole) throws BadRequestException {
        if (this.roleService.existName(newRole.getName())) {
            throw new BadRequestException("Name is exist");
        }
        Role role = this.roleService.createRole(newRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PutMapping("/roles")
    @ApiMessage("update a role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role newRole) throws BadRequestException {
        if (!this.roleService.existId(newRole.getId())) {
            throw new BadRequestException("Id is not exist");
        }
        if (this.roleService.existName(newRole.getName()) && newRole.getName() != this.roleService.fetchRoleById(newRole.getId()).getName()) {
            throw new BadRequestException("Name is exist");
        }
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
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("fetch a role")
    public ResponseEntity<Role> getRole(@PathVariable long id) throws BadRequestException {
        if (!this.roleService.existId(id)) {
            throw new BadRequestException("Id is not exist");
        }
        return ResponseEntity.ok(this.roleService.fetchRoleById(id));
    }

}
