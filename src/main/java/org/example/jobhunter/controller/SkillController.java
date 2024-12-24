package org.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.example.jobhunter.domain.Skill;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.exception.IdInvalidException;
import org.example.jobhunter.service.SkillService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage(value = "Create a skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill newSkill) throws IdInvalidException {
        if (this.skillService.handleExistSkill(newSkill)) {
            throw new IdInvalidException("name is already taken");
        }
        Skill skill = this.skillService.handleCreateSkill(newSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(skill);
    }

    @PutMapping("/skills")
    @ApiMessage(value = "Update a skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill updateSkill) throws IdInvalidException {
        if (this.skillService.handleExistSkill(updateSkill)) {
            throw new IdInvalidException("name is already taken");
        }
        Skill skill = this.skillService.handleUpdateSkill(updateSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(skill);
    }

    @GetMapping("/skills/{id}")
    @ApiMessage(value = "Fetch a skill")
    public ResponseEntity<Skill> getASkill(@PathVariable long id) throws IdInvalidException {
        Skill skill = this.skillService.handleFetchASkill(id).get();
        if (skill == null) {
            throw new IdInvalidException("id is invalid");
        }
        return ResponseEntity.status(HttpStatus.OK).body(skill);
    }

    @GetMapping("/skills")
    @ApiMessage(value = "Fetch skills")
    public ResponseEntity<ResPaginationDTO> getAllSkills(
            @Filter
            Specification<Skill> spec,
            Pageable pageable
    ) {
        ResPaginationDTO resPaginationDTO = this.skillService.handleFetchAllSkills(spec,pageable);
        return ResponseEntity.ok().body(resPaginationDTO);
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage(value = "Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable long id) throws IdInvalidException {
        this.skillService.handleDeleteASkill(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
