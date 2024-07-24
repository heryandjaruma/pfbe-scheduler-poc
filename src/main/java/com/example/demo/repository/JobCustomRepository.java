package com.example.demo.repository;

import com.example.demo.model.Job;
import reactor.core.publisher.Flux;

public interface JobCustomRepository {

  Flux<Job> findByLastRunAtAndLastScheduledAtLessThanEqualCurrentTimeMillisOrNull(Long currentTimeMillis);

}
