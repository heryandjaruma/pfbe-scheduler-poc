package com.example.demo.repository;

import com.example.demo.model.Run;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Slf4j
@Repository
public class RunCustomRepositoryImpl implements RunCustomRepository {

  @Autowired
  private ReactiveMongoTemplate reactiveMongoTemplate;

  @Override
  public Flux<Run> findByScheduledToRunAtLessThanEqualCurrentTimeMillisAndStatusIsScheduled(Long currentTimeMillis) {
    Criteria criteriaLessThanEqual = Criteria.where("scheduledToRunAt").lte(currentTimeMillis);
    Criteria criteriaIsScheduled = Criteria.where("status").is(Run.Status.SCHEDULED.name());
    Query query = new Query(new Criteria().andOperator(criteriaLessThanEqual, criteriaIsScheduled));
    log.debug("Query: {}", query);
    return reactiveMongoTemplate.find(query, Run.class);
  }
}
