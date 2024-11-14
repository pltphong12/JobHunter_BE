package org.example.jobhunter.domain.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.jobhunter.domain.Company;
import org.example.jobhunter.domain.User;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    @Enumerated(EnumType.STRING)
    private User.GenderEnum gender;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private ResCompany company;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResCompany {
        private long id;
        private String name;
    }

    public ResUserDTO(long id, @NotBlank(message = "Email isn't blank") String email, String name, User.GenderEnum gender, String address, int age, Instant updatedAt, Instant createdAt, ResCompany company) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.age = age;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.company = company;
    }
}
