package com.example.demo.repository;

import com.example.demo.model.Job;

import java.util.List;

public interface JobCustomRepository {

  List<Job> findByLastRunAtAndLastScheduledAtLessThanCurrentTimeMillisOrNull(Long currentTimeMillis);

}
