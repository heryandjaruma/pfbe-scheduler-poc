package com.example.demo.repository;

import com.example.demo.model.Run;
import reactor.core.publisher.Flux;

public interface RunCustomRepository {
  Flux<Run> findByScheduledToRunAtLessThanEqualCurrentTimeMillisAndStatusIsScheduled(Long currentTimeMillis);
}
