package org.example.jobhunter.controller;

import jakarta.validation.Valid;
import org.example.jobhunter.domain.DTO.LoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Gửi thông tin input gồm username và password vào security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        // Xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return ResponseEntity.ok().body(loginDTO);
    }
}
