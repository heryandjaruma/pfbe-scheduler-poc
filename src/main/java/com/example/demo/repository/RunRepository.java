package com.example.demo.repository;

import com.example.demo.model.Run;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RunRepository extends ReactiveMongoRepository<Run, String> {
}
