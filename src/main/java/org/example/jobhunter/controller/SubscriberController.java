package org.example.jobhunter.controller;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.jobhunter.domain.Skill;
import org.example.jobhunter.domain.Subscriber;
import org.example.jobhunter.service.SkillService;
import org.example.jobhunter.service.SubscriberService;
import org.example.jobhunter.service.UserService;
import org.example.jobhunter.util.anotation.ApiMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;
    private final UserService userService;
    private final SkillService skillService;

    public SubscriberController(SubscriberService subscriberService, UserService userService, SkillService skillService) {
        this.subscriberService = subscriberService;
        this.userService = userService;
        this.skillService = skillService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber) throws BadRequestException {
        if (this.userService.isExistEmail(subscriber.getEmail())) {
            throw new BadRequestException("email already exist");
        }
        List<Skill> skills = new ArrayList<>();
        for (Skill skill : subscriber.getSkills()) {
            if (this.skillService.handleExistSkillById(skill) == false) {
                throw new BadRequestException("skill isn't valid");
            }else {
                Skill skill1 = this.skillService.handleFetchASkill(skill.getId()).get();
                skills.add(skill1);
                subscriber.setSkills(skills);
            }
        }
        Subscriber newSubscriber = subscriberService.createSubscriber(subscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSubscriber);
    }

    @PutMapping("/subscribers")
    @ApiMessage("update subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws BadRequestException {
        if (!this.subscriberService.isExistingSubscriber(subscriber)) {
            throw new BadRequestException("subscriber isn't valid");
        }
        List<Skill> skills = new ArrayList<>();
        for (Skill skill : subscriber.getSkills()) {
            if (this.skillService.handleExistSkillById(skill) == false) {
                throw new BadRequestException("skill isn't valid");
            }else {
                Skill skill1 = this.skillService.handleFetchASkill(skill.getId()).get();
                skills.add(skill1);
                subscriber.setSkills(skills);
            }
        }
        Subscriber newSubscriber = subscriberService.updateSubscriber(subscriber);
        return ResponseEntity.status(HttpStatus.OK).body(newSubscriber);
    }
}
