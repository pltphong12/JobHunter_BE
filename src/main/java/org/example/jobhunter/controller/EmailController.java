package org.example.jobhunter.controller;

import org.example.jobhunter.service.EmailService;
import org.example.jobhunter.service.SubscriberService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("send email")
    public void sendEmail() {
        this.subscriberService.sendSubscribersEmailJobs();
    }
}
