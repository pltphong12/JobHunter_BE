package org.example.jobhunter.controller;

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
    @PostMapping("/user")
    public User createUser(@RequestBody User newUser){
        this.userService.handleCreateUser(newUser);
        return newUser;
    }

    // Read User
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable long id){
        return this.userService.handleFetchUserById(id);
    }
    @GetMapping("/user")
    public List<User> getAllUsers(){
        return this.userService.handlefetchAllUser();
    }

    // Update User
    @PutMapping("/user")
    public User updateUser( @RequestBody User updatedUser){
        return this.userService.handleUpdateUser(updatedUser);
    }

    // Delete User
    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") long id){
        this.userService.handleDeleteUser(id);
        return "User Id: " + id + " deleted";
    }
}

