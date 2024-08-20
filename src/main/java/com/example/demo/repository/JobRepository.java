package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.demo.domain.Job;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

}
