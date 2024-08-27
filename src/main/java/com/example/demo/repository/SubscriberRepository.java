package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.Subscriber;

@Repository
public interface SubscriberRepository
        extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<SubscriberRepository> {
    boolean existsByEmail(String email);

    Subscriber findByEmail(String email);
}
