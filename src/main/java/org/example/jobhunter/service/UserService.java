package org.example.jobhunter.service;

import org.example.jobhunter.domain.Company;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.domain.response.ResUserDTO;
import org.example.jobhunter.domain.User;
import org.example.jobhunter.repository.CompanyRepository;
import org.example.jobhunter.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public User handleCreateUser(User user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        long companyId = user.getCompany().getId();
        Optional<Company> company = this.companyRepository.findById(companyId);
        if (company.isPresent()) {
            user.setCompany(company.get());
        }else {
            user.setCompany(null);
        }
        return userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        userRepository.deleteById(id);
    }

    public boolean isExistEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User handleFetchUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public ResPaginationDTO handleFetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta mt = new ResPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageUser.getSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        //remove sentitive data
        List<ResUserDTO> listUser = pageUser.getContent().stream().map(item -> new ResUserDTO(
                item.getId(),
                item.getEmail(),
                item.getName(),
                item.getGender(),
                item.getAddress(),
                item.getAge(),
                item.getUpdatedAt(),
                item.getCreatedAt()
        )).collect(Collectors.toList());

        rs.setResult(listUser);

        return rs;
    }

    public User handleUpdateUser(User user) {
        User currentUser = this.userRepository.findById(user.getId()).orElse(null);
        if (currentUser != null){
            if (user.getName() != null){
                currentUser.setName(user.getName());
            }
            if (user.getEmail() != null){
                currentUser.setEmail(user.getEmail());
            }
            if (user.getAge() != 0){
                currentUser.setAge(user.getAge());
            }
            if (user.getAddress() != null){
                currentUser.setAddress(user.getAddress());
            }
            Company company = this.companyRepository.findById(user.getCompany().getId()).orElse(null);
            if (user.getCompany() != null && company != null){
                currentUser.setCompany(user.getCompany());
            }
            return this.userRepository.save(currentUser);
        }
        return null;
    }

    public User handleFetchUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email){
        User user = this.userRepository.findByEmail(email);
        if (user != null){
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User handleFetchUserByEmailAndRefreshToken(String email, String refreshToken){
        return this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }
}
