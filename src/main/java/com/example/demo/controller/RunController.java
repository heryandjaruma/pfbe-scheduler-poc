package com.example.demo.controller;

import com.example.demo.exception.DataNotFoundException;
import com.example.demo.model.Job;
import com.example.demo.model.Run;
import com.example.demo.repository.RunRepository;
import com.example.demo.web.SaveRunWebRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(RunController.PATH)
@Slf4j
public class RunController {

  public static final String PATH = "/run";
  public static final String ID = "/{id}";

  @Autowired
  private RunRepository runRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public Mono<Run> saveRun(@RequestBody SaveRunWebRequest webRequest) {
    return runRepository.save(Run.builder()
            .jobId(webRequest.getJobId())
            .status(webRequest.getStatus())
            .scheduledToRunAt(webRequest.getScheduledToRunAt())
            .completedAt(webRequest.getCompletedAt())
            .build())
        .doOnError(ex -> log.error("Failed to save Run for Job ID {}", webRequest.getJobId(), ex))
        .doOnSuccess(res -> log.info("Successfully save Run {}", res));
  }

  @GetMapping(ID)
  @ResponseStatus(HttpStatus.OK)
  public Mono<Run> get(@PathVariable String id) {
    return runRepository.findById(id)
        .switchIfEmpty(Mono.error(new DataNotFoundException(Run.class.getName(),
            HttpStatus.NOT_FOUND.toString())))
        .doOnError(ex -> log.error("Could not find Run with id {}", id, ex))
        .doOnSuccess(res -> log.info("Successfully find Run {}", res));
  }

  @PutMapping(ID)
  @ResponseStatus(HttpStatus.OK)
  public Mono<Run> update(@PathVariable String id, @RequestBody SaveRunWebRequest webRequest) {
    return runRepository.findById(id)
        .switchIfEmpty(Mono.error(new DataNotFoundException(Job.class.getName(),
            HttpStatus.NOT_FOUND.toString())))
        .map(run -> run.toBuilder()
            .jobId(webRequest.getJobId())
            .status(webRequest.getStatus())
            .scheduledToRunAt(webRequest.getScheduledToRunAt())
            .completedAt(webRequest.getCompletedAt())
            .build())
        .flatMap(run -> runRepository.save(run))
        .doOnError(ex -> log.error("Could not find Job with id {}", id, ex))
        .doOnSuccess(res -> log.info("Successfully update Job {}", res));
  }
}
