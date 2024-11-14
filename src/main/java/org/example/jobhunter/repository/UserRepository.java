package org.example.jobhunter.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.jobhunter.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String username);
    boolean existsByEmail(String email);
    User findByEmailAndRefreshToken(String email, String refreshToken);
    Page<User> findAll(Specification<User> spec, Pageable pageable);
}
