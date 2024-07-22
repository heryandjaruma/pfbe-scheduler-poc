package com.example.demo.repository;

import com.example.demo.model.Job;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface JobRepository extends ReactiveMongoRepository<Job, String> {
}
