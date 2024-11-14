package org.example.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "username not blank")
    private String username;
    @NotBlank(message = "password not blank")
    private String password;

}
