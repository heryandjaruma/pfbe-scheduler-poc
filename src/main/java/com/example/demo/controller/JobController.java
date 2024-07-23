package com.example.demo.controller;

import com.example.demo.exception.DataNotFoundException;
import com.example.demo.model.Job;
import com.example.demo.repository.JobRepository;
import com.example.demo.web.SaveJobWebRequest;
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
@RequestMapping(JobController.PATH)
@Slf4j
public class JobController {

  public static final String PATH = "/job";
  public static final String ID = "/{id}";

  @Autowired
  private JobRepository jobRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public Mono<Job> save(@RequestBody SaveJobWebRequest webRequest) {
    return jobRepository.save(Job.builder()
            .name(webRequest.getName())
            .description(webRequest.getDescription())
            .cronExpression(webRequest.getCronExpression())
            .misfire(webRequest.getMisfire())
            .endpoint(webRequest.getEndpoint())
            .headers(webRequest.getHeaders())
            .httpMethod(webRequest.getHttpMethod())
            .body(webRequest.getBody())
        .build());
  }

  @GetMapping(ID)
  @ResponseStatus(HttpStatus.OK)
  public Mono<Job> get(@PathVariable String id) {
    return jobRepository.findById(id)
        .switchIfEmpty(Mono.error(new DataNotFoundException(Job.class.getName(), HttpStatus.NOT_FOUND.toString())))
        .doOnError(ex -> log.error("Could not find Job with id {}", id, ex))
        .doOnSuccess(res -> log.info("Successfully find Job {}", res));
  }

  @PutMapping(ID)
  @ResponseStatus(HttpStatus.OK)
  public Mono<Job> update(@PathVariable String id, @RequestBody SaveJobWebRequest webRequest) {
    return jobRepository.findById(id)
        .switchIfEmpty(Mono.error(new DataNotFoundException(Job.class.getName(), HttpStatus.NOT_FOUND.toString())))
        .map(job -> job.toBuilder()
            .name(webRequest.getName())
            .description(webRequest.getDescription())
                . cronExpression(webRequest.getCronExpression())
                .misfire(webRequest.getMisfire())
                .endpoint(webRequest.getEndpoint())
                .headers(webRequest.getHeaders())
                .httpMethod(webRequest.getHttpMethod())
                .body(webRequest.getBody())
                .build())
        .flatMap(job -> jobRepository.save(job))
        .doOnError(ex -> log.error("Could not find Job with id {}", id, ex))
        .doOnSuccess(res -> log.info("Successfully update Job {}", res));
  }
}
