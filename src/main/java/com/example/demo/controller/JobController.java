package com.example.demo.controller;

import com.example.demo.model.Job;
import com.example.demo.repository.JobRepository;
import com.example.demo.web.SaveJobWebRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class JobController {

  @Autowired
  private JobRepository jobRepository;

  @PostMapping("/job")
  public Mono<Job> saveJob(@RequestBody SaveJobWebRequest webRequest) {
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
}
