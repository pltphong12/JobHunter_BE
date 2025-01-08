package org.example.jobhunter.service;

import org.example.jobhunter.domain.Job;
import org.example.jobhunter.domain.Skill;
import org.example.jobhunter.domain.Subscriber;
import org.example.jobhunter.domain.response.email.ResEmailJob;
import org.example.jobhunter.repository.JobRepository;
import org.example.jobhunter.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Subscriber createSubscriber(Subscriber subscriber) {
        return subscriberRepository.save(subscriber);
    }

    public boolean isExistingSubscriber(Subscriber subscriber) {
        return this.subscriberRepository.existsById(subscriber.getId());
    }

    public Subscriber updateSubscriber(Subscriber subscriber) {
        Subscriber updatedSubscriber = this.subscriberRepository.findById(subscriber.getId()).orElse(null);
        updatedSubscriber.setSkills(subscriber.getSkills());
        return this.subscriberRepository.save(updatedSubscriber);
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                         List<ResEmailJob> arr = listJobs.stream().map(
                         job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new
                        ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

}
