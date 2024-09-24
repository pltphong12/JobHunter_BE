package org.example.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.jobhunter.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
