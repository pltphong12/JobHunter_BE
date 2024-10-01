package org.example.jobhunter.controller;

import org.example.jobhunter.exception.IdInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.jobhunter.domain.User;
import org.example.jobhunter.service.UserService;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Create User
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User newUser){
        User user = this.userService.handleCreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // Read User
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) throws IdInvalidException {
        int userId;
        try {
            userId = Integer.parseInt(id);
        }catch (Exception e){
            throw new IdInvalidException("id must be an integer");
        }
        User user = this.userService.handleFetchUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = this.userService.handleFetchAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    // Update User
    @PutMapping("/users")
    public ResponseEntity<User> updateUser( @RequestBody User updatedUser){
        User user = this.userService.handleUpdateUser(updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // Delete User
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) throws IdInvalidException {
        int userId;
        try {
            userId = Integer.parseInt(id);
        }catch (Exception e){
            throw new IdInvalidException("id must be an integer");
        }
        this.userService.handleDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

