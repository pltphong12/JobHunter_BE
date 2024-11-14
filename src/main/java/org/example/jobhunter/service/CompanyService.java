package org.example.jobhunter.service;

import org.example.jobhunter.domain.Company;
import org.example.jobhunter.domain.User;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.repository.CompanyRepository;
import org.example.jobhunter.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResPaginationDTO handleFetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        ResPaginationDTO resPaginationDTO = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();
        Page<Company> companies = this.companyRepository.findAll(spec, pageable);
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companies.getTotalPages());
        meta.setTotal(companies.getTotalElements());

        resPaginationDTO.setMeta(meta);
        resPaginationDTO.setResult(companies.getContent());
        return resPaginationDTO;
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> currentCompany = this.companyRepository.findById(company.getId());
        if (currentCompany.isPresent()) {
            if (company.getName() != null && !company.getName().equals(currentCompany.get().getName())){
                currentCompany.get().setName(company.getName());
            }
            if (company.getDescription() != null && !company.getDescription().equals(currentCompany.get().getDescription())){
                currentCompany.get().setDescription(company.getDescription());
            }
            if (company.getAddress() != null && !company.getAddress().equals(currentCompany.get().getAddress())){
                currentCompany.get().setAddress(company.getAddress());
            }
            if (company.getLogo() != null && !company.getLogo().equals(currentCompany.get().getLogo())){
                currentCompany.get().setLogo(company.getLogo());
            }
            return companyRepository.save(currentCompany.get());
        }
        return null;
    }

    public void handleDeleteCompany(long id ) {
        Optional<Company> currentCompany = this.companyRepository.findById(id);
        if (currentCompany.isPresent()) {
            List<User> users = currentCompany.get().getUsers();
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
}
