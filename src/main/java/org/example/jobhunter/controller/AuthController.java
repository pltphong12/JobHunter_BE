package org.example.jobhunter.controller;

import jakarta.validation.Valid;
import org.example.jobhunter.domain.request.ReqLoginDTO;
import org.example.jobhunter.domain.response.ResLoginDTO;
import org.example.jobhunter.domain.response.ResUserDTO;
import org.example.jobhunter.domain.response.ResUserGetAccount;
import org.example.jobhunter.domain.User;
import org.example.jobhunter.exception.IdInvalidException;
import org.example.jobhunter.service.UserService;
import org.example.jobhunter.util.SecurityUtil;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final SecurityUtil securityUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService, ModelMapper modelMapper) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/auth/login")
    @ApiMessage(value = "Login Successful")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // Gửi thông tin input gồm username và password vào security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
        // Xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);


        SecurityContextHolder.getContext().setAuthentication(authentication);
        User currentUser = this.userService.handleFetchUserByUsername(loginDTO.getUsername());

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName(),
                currentUser.getRole()
        );
        resLoginDTO.setUser(userLogin);
        String accessToken = this.securityUtil.createToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String refreshToken = this.securityUtil.refreshToken(userLogin.getEmail(), resLoginDTO);
        // update user
        this.userService.updateUserToken(refreshToken, userLogin.getEmail());
        // set cookie
        ResponseCookie springCookie = ResponseCookie.from("refresh_token", refreshToken).httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, springCookie.toString()).body(resLoginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage(value = "fetch a account")
    public ResponseEntity<ResUserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.handleFetchUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName(),
                currentUser.getRole()
        );
        ResUserGetAccount userGetAccount = new ResUserGetAccount(userLogin);
        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage(value = "Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token") String refreshToken
    ) throws IdInvalidException {
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();
        User currentUser = this.userService.handleFetchUserByUsername(email);
        // check email valid
        if (this.userService.handleFetchUserByEmailAndRefreshToken(email, refreshToken) == null){
            throw new IdInvalidException("Refresh Token invalid");
        }
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName(),
                currentUser.getRole()
        );
        resLoginDTO.setUser(userLogin);
        String accessToken = this.securityUtil.createToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(accessToken);

        // Create refresh token
        String new_refreshToken = this.securityUtil.refreshToken(email, resLoginDTO);
        // update user
        this.userService.updateUserToken(new_refreshToken, userLogin.getEmail());
        // set cookie
        ResponseCookie springCookie = ResponseCookie.from("refresh_token", new_refreshToken).httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, springCookie.toString()).body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage(value = "Logout Successful")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : " ";
        // check access token
        if (email.equals(" ")) {
            throw new IdInvalidException("Access Token invalid");
        }
        // update refresh token = null
        this.userService.updateUserToken(null, email);
        // remove refresh token in cookie
        ResponseCookie springCookie = ResponseCookie.from("refresh_token", null).httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, springCookie.toString()).build();
    }

    @PostMapping("/auth/register")
    @ApiMessage("register a account")
    public ResponseEntity<ResUserDTO> register(@Valid @RequestBody User newUser) throws IdInvalidException {
        if (this.userService.isExistEmail(newUser.getEmail())) {
            throw new IdInvalidException("Email already in use");
        }
        User user = this.userService.handleRegisterUser(newUser);
        ResUserDTO resUserDTO = modelMapper.map(user, ResUserDTO.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(resUserDTO);
    }
}
