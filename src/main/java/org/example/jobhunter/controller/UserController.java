package org.example.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.example.jobhunter.domain.response.ResPaginationDTO;
import org.example.jobhunter.domain.response.ResUserDTO;
import org.example.jobhunter.exception.IdInvalidException;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.example.jobhunter.domain.User;
import org.example.jobhunter.service.UserService;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {

        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    // Create User
    @PostMapping("/users")
    @ApiMessage(value = "create a user")
    public ResponseEntity<ResUserDTO> createUser(@Valid @RequestBody User newUser) throws BadCredentialsException {
        if (userService.isExistEmail(newUser.getEmail())) {
            throw new BadCredentialsException("Email already exists");
        }
        User user = this.userService.handleCreateUser(newUser);
        ResUserDTO resUserDTO = modelMapper.map(user, ResUserDTO.class);
        if (user.getCompany() != null) {
            ResUserDTO.ResCompany resCompany = modelMapper.map(user.getCompany(), ResUserDTO.ResCompany.class);
            resUserDTO.setCompany(resCompany);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resUserDTO);
    }

    // Read User
    @GetMapping("/users/{id}")
    @ApiMessage(value = "fetch a user")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable String id) throws IdInvalidException, BadCredentialsException {
        int userId;
        try {
            userId = Integer.parseInt(id);
        }catch (Exception e){
            throw new IdInvalidException("id must be an integer");
        }
        if (userService.handleFetchUserById(userId) == null) {
            throw new BadCredentialsException("user does not exist");
        }
        User user = this.userService.handleFetchUserById(userId);
        ResUserDTO resUserDTO = modelMapper.map(user, ResUserDTO.class);
        if (user.getCompany() != null) {
            ResUserDTO.ResCompany resCompany = modelMapper.map(user.getCompany(), ResUserDTO.ResCompany.class);
            resUserDTO.setCompany(resCompany);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resUserDTO);
    }
    @GetMapping("/users")
    @ApiMessage(value = "fetch all users")
    public ResponseEntity<ResPaginationDTO> getAllUsers(
            @Filter
            Specification<User> spec,
            Pageable pageable
    ) {
        ResPaginationDTO rs = this.userService.handleFetchAllUsers(spec, pageable);
        return ResponseEntity.ok(rs);
    }

    // Update User
    @PutMapping("/users")
    @ApiMessage(value = "update a user")
    public ResponseEntity<ResUserDTO> updateUser( @RequestBody User updatedUser) throws BadCredentialsException{
        if (userService.handleFetchUserById(updatedUser.getId()) == null) {
            throw new BadCredentialsException("user does not exist");
        }
        User user = this.userService.handleUpdateUser(updatedUser);
        ResUserDTO resUserDTO = modelMapper.map(user, ResUserDTO.class);
        if (user.getCompany() != null) {
            ResUserDTO.ResCompany resCompany = modelMapper.map(user.getCompany(), ResUserDTO.ResCompany.class);
            resUserDTO.setCompany(resCompany);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resUserDTO);
    }

    // Delete User
    @DeleteMapping("/users/{id}")
    @ApiMessage(value = "delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") String id) throws IdInvalidException,BadCredentialsException {
        int userId;
        try {
            userId = Integer.parseInt(id);
        }catch (Exception e){
            throw new IdInvalidException("id must be an integer");
        }
        if (userService.handleFetchUserById(userId) == null) {
            throw new BadCredentialsException("user does not exist");
        }
        this.userService.handleDeleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

