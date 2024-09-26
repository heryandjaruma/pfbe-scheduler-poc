package com.example.demo.daemon;

import com.example.demo.model.Job;
import com.example.demo.model.Run;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.RunRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.expression.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Component
public class DaemonWorker {

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private RunRepository runRepository;

  @Scheduled(cron = "0 * * * * *")
  public void triggerAndScheduleNextDaemon() {

    /*
     * Check for ready all Run(s)
     */
    runRepository
        .findByScheduledToRunAtLessThanEqualCurrentTimeMillisAndStatusIsScheduled(System.currentTimeMillis())

        
        /*
         * Update each Run status to STARTED
         */
        .map(run -> run.toBuilder()
            .status(Run.Status.STARTED.name())
            .build())
        .flatMap(run -> runRepository.save(run))
        .onErrorContinue((err, job) -> handleLogError(err))


        /*
         * Do the Job's Run
         */
        .doOnNext(job -> log.info("Job {} run!", job)) // running actual job


        /*
         * Set Status to Finished
         */
        .map(run -> run.toBuilder()
            .status(Run.Status.FINISHED.name())
            .completedAt(System.currentTimeMillis())
            .build())
        .flatMap(run -> runRepository.save(run))

        /*
         * Schedule the next Run for the Job
         */
        .flatMap(run -> jobRepository.findById(run.getJobId()))
        .flatMap(this::scheduleNextRun)

        /*
         * For logging: count how many Run in the Flux
         */
        .count()
        .doOnNext(aLong -> log.info("Count {}", aLong))
        .subscribe();
  }

  public void handleLogError(Throwable ex) {
    if (ex instanceof OptimisticLockingFailureException) {
    } else {
      log.error("Error occurred", ex);
    }
  }

  public Mono<Run> scheduleNextRun(Job job) {
    return Mono.just(job)
        .map(jobToSchedule -> Run.builder()
            .jobId(job.getId())
            .status(Run.Status.SCHEDULED.name())
            .scheduledToRunAt(DaemonWorker.getNextRunSchedule(job.getCronExpression()))
            .build())
        .flatMap(run -> runRepository.save(run));
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
