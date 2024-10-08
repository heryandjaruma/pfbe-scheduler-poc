package com.example.demo.controller;

import com.example.demo.exception.DataNotFoundException;
import com.example.demo.model.Job;
import com.example.demo.model.Run;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.RunRepository;
import com.example.demo.web.SaveJobWebRequest;
import com.example.demo.daemon.DaemonWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Random;

@RestController
@RequestMapping(JobController.PATH)
@Slf4j
public class JobController {

  public static final String PATH = "/job";
  public static final String ID = "/{id}";

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private RunRepository runRepository;

  @Autowired
  private DaemonWorker daemonWorker;


  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public Mono<Job> save(@RequestBody SaveJobWebRequest webRequest) {
    return jobRepository.save(Job.builder()
            .name(webRequest.getName())
            .description(webRequest.getDescription())
            .cronExpression(webRequest.getCronExpression())
            .endpoint(webRequest.getEndpoint())
            .headers(webRequest.getHeaders())
            .httpMethod(webRequest.getHttpMethod())
            .body(webRequest.getBody())
            .build())
        .doOnError(ex -> log.error("Failed to save Job {}", webRequest.getName(), ex))
        .doOnSuccess(res -> log.info("Successfully save Job {}", res))
        .flatMap(job -> daemonWorker.scheduleNextRun(job))
        .flatMap(run -> jobRepository.findById(run.getJobId()));
  }

  @GetMapping(ID)
  @ResponseStatus(HttpStatus.OK)
  public Mono<Job> get(@PathVariable String id) {
    return jobRepository.findById(id)
        .switchIfEmpty(Mono.error(new DataNotFoundException(Job.class.getName(),
            HttpStatus.NOT_FOUND.toString())))
        .doOnError(ex -> log.error("Could not find Job with id {}", id, ex))
        .doOnSuccess(res -> log.info("Successfully find Job {}", res));
  }

  @PutMapping(ID)
  @ResponseStatus(HttpStatus.OK)
  public Mono<Job> update(@PathVariable String id, @RequestBody SaveJobWebRequest webRequest) {
    return jobRepository.findById(id)
        .switchIfEmpty(Mono.error(new DataNotFoundException(Job.class.getName(),
            HttpStatus.NOT_FOUND.toString())))
        .map(job -> job.toBuilder()
            .name(webRequest.getName())
            .description(webRequest.getDescription())
            .cronExpression(webRequest.getCronExpression())
            .endpoint(webRequest.getEndpoint())
            .headers(webRequest.getHeaders())
            .httpMethod(webRequest.getHttpMethod())
            .body(webRequest.getBody())
            .build())
        .flatMap(job -> jobRepository.save(job))
        .doOnError(ex -> log.error("Could not find Job with id {}", id, ex))
        .doOnSuccess(res -> log.info("Successfully update Job {}", res));
  }

  @GetMapping("/ManualSetNextSchedule")
  public Boolean manualSetNextSchedule() {
    jobRepository.findByLastRunAtAndLastScheduledAtLessThanEqualCurrentTimeMillisOrNull(System.currentTimeMillis())
        .map(job -> Run.builder()
            .jobId(job.getId())
            .status(Run.Status.SCHEDULED.name())
            .scheduledToRunAt(DaemonWorker.getNextRunSchedule(job.getCronExpression()))
            .build())
        .flatMap(run -> runRepository.save(run))
        .subscribe();
    return Boolean.TRUE;
  }

  @GetMapping("/create-random-jobs")
  public Boolean createRandomJobs(@RequestParam Integer amount) {
    for (int i = 0; i < amount; i++) {
      jobRepository.save(Job.builder()
          .name(generateRandomString())
          .description(generateRandomString())
          .cronExpression("0 * * * * *")
          .endpoint(generateRandomString())
          .httpMethod("POST")
          .build()).flatMap(job -> daemonWorker.scheduleNextRun(job)).subscribe();
    }
    log.info("Done creating {} jobs", amount);
    return Boolean.TRUE;
  }

  public static String generateRandomString() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder randomString = new StringBuilder(12);
    Random random = new Random();

    for (int i = 0; i < 12; i++) {
      int index = random.nextInt(characters.length());
      randomString.append(characters.charAt(index));
    }

    return randomString.toString();
  }
}
