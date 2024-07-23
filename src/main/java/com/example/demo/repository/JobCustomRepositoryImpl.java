package com.example.demo.repository;


import com.example.demo.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class JobCustomRepositoryImpl implements JobCustomRepository {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<Job> findByLastRunAtAndLastScheduledAtLessThanCurrentTimeMillisOrNull(Long currentTimeMillis) {
    Criteria criteriaLessThan = Criteria.where("lastRunAt").lte(currentTimeMillis)
        .and("lastScheduleAt").lte(currentTimeMillis);
    Criteria criteriaNull = Criteria.where("lastRunAt").isNull()
        .and("lastScheduleAt").isNull();
    Query query = new Query(new Criteria().orOperator(criteriaLessThan, criteriaNull));
    log.debug("Query: {}", query);
    return mongoTemplate.find(query, Job.class);
  }
}