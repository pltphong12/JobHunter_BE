package org.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.example.jobhunter.domain.Company;
import org.example.jobhunter.domain.DTO.ResPaginationDTO;
import org.example.jobhunter.exception.IdInvalidException;
import org.example.jobhunter.service.CompanyService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> addCompany(@Valid @RequestBody Company createCompany) {
        Company company = this.companyService.handleCreateCompany(createCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    // Get company with filter and pagination
    @GetMapping("/companies")
    @ApiMessage(value = "fetch all companies")
    public ResponseEntity<ResPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec,
            Pageable pageable) {
        ResPaginationDTO companies = this.companyService.handleFetchAllCompanies(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(companies);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company updateCompany) {
        Company company = this.companyService.handleUpdateCompany(updateCompany);
        return ResponseEntity.status(HttpStatus.OK).body(company);
    }


    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable String id) throws IdInvalidException {
        long companyId;
        try {
            companyId = Integer.parseInt(id);
        }catch (Exception e){
            throw new IdInvalidException("id must be an integer");
        }
        this.companyService.handleDeleteCompany(companyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
