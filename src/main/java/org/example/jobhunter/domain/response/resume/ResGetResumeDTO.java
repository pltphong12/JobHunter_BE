package org.example.jobhunter.domain.response.resume;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.jobhunter.domain.Resume;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResGetResumeDTO {
    private int id;
    private String email;
    private String url;

    @Enumerated(EnumType.STRING)
    private Resume.StatusEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    private ResJobInResumeDTO job;
    private ResUserInResumeDTO user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResJobInResumeDTO {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResUserInResumeDTO {
        private long id;
        private String name;
    }
}
