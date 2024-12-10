package org.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.jobhunter.domain.Permission;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.service.PermissionService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("create a permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission newPermission) throws BadRequestException {
        if (this.permissionService.isPermissionExist(newPermission)) {
            throw new BadRequestException("Permission already exist");
        }
        Permission permission = this.permissionService.createPermission(newPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @PutMapping("/permissions")
    @ApiMessage("update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission newPermission) throws BadRequestException {
        if (!this.permissionService.isPermission(newPermission)){
            throw new BadRequestException("Id is not a valid permission");
        }
        Permission permission = this.permissionService.updatePermission(newPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @GetMapping("/permissions")
    @ApiMessage("fetch all permissions")
    public ResponseEntity<ResPaginationDTO> fetchAllPermissions(
            @Filter Specification<Permission> specification,
            Pageable pageable
    ) {
        ResPaginationDTO resPaginationDTO = this.permissionService.handleFetchAllPermission(specification, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(resPaginationDTO);
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) throws BadRequestException {
        if (this.permissionService.getPermissionById(id) == null) {
            throw new BadRequestException("Id is not a valid permission");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
