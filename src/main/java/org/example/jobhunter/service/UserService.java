package org.example.jobhunter.service;

import org.example.jobhunter.domain.User;
import org.example.jobhunter.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        userRepository.deleteById(id);
    }

    public User handleFetchUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> handleFetchAllUser(){
        return userRepository.findAll();
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
            if (user.getPassword() != null){
                currentUser.setPassword(user.getPassword());
            }
            return this.userRepository.save(currentUser);
        }
        return null;
    }

    public User handleFetchUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
