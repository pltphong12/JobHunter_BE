package org.example.jobhunter.domain.DTO;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank(message = "username not blank")
    private String username;
    @NotBlank(message = "password not blank")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
