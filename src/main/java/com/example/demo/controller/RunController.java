package com.example.demo.controller;

import com.example.demo.model.Run;
import com.example.demo.repository.RunRepository;
import com.example.demo.web.SaveRunWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class RunController {

  @Autowired
  private RunRepository runRepository;

  @PostMapping("/run")
  public Mono<Run> saveRun(@RequestBody SaveRunWebRequest webRequest) {
    return runRepository.save(Run.builder()
        .jobId(webRequest.getJobId())
        .status(webRequest.getStatus())
        .scheduleAt(webRequest.getScheduleAt())
        .startedAt(webRequest.getStartedAt())
        .expiredAt(webRequest.getExpiredAt())
        .completedAt(webRequest.getCompletedAt())
        .build());
  }
}
