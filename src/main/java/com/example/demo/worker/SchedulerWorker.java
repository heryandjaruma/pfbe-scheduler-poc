package com.example.demo.worker;

import com.example.demo.model.Job;
import com.example.demo.model.Run;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.RunRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class SchedulerWorker {

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private RunRepository runRepository;

  @Scheduled(cron = "0 * * * * *")
  public void readyToRun() {
    // check scheduled runs every minute and make it RUN
    //
    // STEPS
    // 1. get all runs where the scheduledToRunAt is less than time.now and status is SCHEDULED
    // 2. update its startedAt and save to database
    // 3. if save failed, log (it means other pod has already taking care of it)
    // 4. if save success, then execute
    // 5. then create a new run
    log.info("ReadyToRun worker is fired.");

    runRepository.findByScheduledToRunAtLessThanEqualCurrentTimeMillisAndStatusIsScheduled(System.currentTimeMillis())
        .map(run -> run.toBuilder()
            .status(Run.Status.STARTED.name())
            .build())
        .flatMap(run -> runRepository.save(run))
        .onErrorContinue((err, job) -> log.error("Error when processing Run {}", job, err))
        .doOnNext(job -> log.info("Job {} run!", job)) // running actual job
        .map(run -> run.toBuilder()
            .status(Run.Status.FINISHED.name())
            .completedAt(System.currentTimeMillis())
            .build())
        .flatMap(run -> runRepository.save(run))
        .doOnNext(run -> log.info("Run with ID {} for Job {} has finished", run.getId(), run.getJobId()))
        .flatMap(run -> jobRepository.findById(run.getJobId()))
        .flatMap(this::scheduleNextRun)
        .subscribe();
  }

  public Mono<Run> scheduleNextRun(Job job) {
    return Mono.just(job)
        .map(jobToSchedule -> Run.builder()
            .jobId(job.getId())
            .status(Run.Status.SCHEDULED.name())
            .scheduledToRunAt(SchedulerWorker.getNextRunSchedule(job.getCronExpression()))
            .build())
        .flatMap(run -> runRepository.save(run))
        .doOnError(ex -> log.error("Error when scheduling next run for Job ID {}", job.getId()))
        .doOnSuccess(res -> log.info("Successfully scheduling next run for Job ID {}", res.getJobId()));
  }

  public static Long getNextRunSchedule(String cronExpression) {
    try {
      CronExpression cronTrigger = CronExpression.parse(cronExpression);
      return toEpochFromDateTime(cronTrigger.next(LocalDateTime.now()));
    } catch (ParseException e) {
      throw new RuntimeException("Error when parsing Cron Expression: " + e.getMessage(), e);
    }
  }

  public static long toEpochFromDateTime(LocalDateTime date) {
    return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }
}
