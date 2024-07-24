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
  public void readyToSchedule() {
    log.info("readyToSchedule is fired.");
    // check available jobs every minute and scheduling its next Run
    //
    // STEPS
    // 1. get all jobs where the lastRunAt AND lastScheduledAt are less than time.now OR null
    // 2. update lastScheduledAt and save it to database
    // 3. if save failed, cancel execution (it means other pod has already taking care of it)
    // 5. if save success, then create a new Run and save it to database

    jobRepository.findByLastRunAtAndLastScheduledAtLessThanCurrentTimeMillisOrNull(System.currentTimeMillis())
        .map(job -> job.toBuilder()
            .lastScheduledAt(System.currentTimeMillis())
            .build())
        .flatMap(job -> jobRepository.save(job))
        .onErrorContinue((err, obj) -> log.error("Error when processing Job {}", obj, err))
        .map(job -> Run.builder()
            .jobId(job.getId())
            .status(Job.Status.SCHEDULED.name())
            .scheduledToRunAt(getNextRunSchedule(job.getCronExpression()))
            .build())
        .flatMap(run -> runRepository.save(run))
        .doOnNext(run -> log.info("Next Run for Job ID {} has been scheduled", run.getJobId()))
        .subscribe();
  }

//  @Scheduled(cron = "* * * * * *")
  public void readyToRun() {
    // makes all scheduled runs to RUN
    //
    // STEPS
    // 1. get all runs where the scheduledToRunAt is less than time.now and startedAt is null
    // 2. update its startedAt and save to database
    // 3. if save failed, cancel execution (it means other pod has already taking care of it)
    // 4. if save success, then execute (fire and forget)
    // 5. at the end, update its job's lastRunAt
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
