package org.example.jobhunter.repository;

import org.example.jobhunter.domain.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

}
