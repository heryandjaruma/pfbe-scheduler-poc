package com.example.demo.worker;

import com.example.demo.model.Job;
import com.example.demo.model.Run;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.RunRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@Component
public class SchedulerWorker {

  @Autowired
  private JobRepository jobRepository;

  @Autowired
  private RunRepository runRepository;

//  @Scheduled(cron = "* * * * * *")
  public void readyToSchedule() {
    // check a job to be scheduled by creating its next run
    //
    // STEPS
    // 1. get all jobs where the lastRunAt AND lastScheduledAt are less than time.now OR null
    // 2. create a new Run and save it to database
    // 3. update lastScheduledAt and save it to database

    Mono.just(jobRepository.findByLastRunAtAndLastScheduledAtLessThanCurrentTimeMillisOrNull(System.currentTimeMillis()))
        .flatMapMany(Flux::fromIterable)
        .map(job -> Run.builder()
            .jobId(job.getId())
            .status(Job.Status.SCHEDULED.name())
            .scheduledToRunAt(getNextRunSchedule(job.getCronExpression()))
            .build())
        .flatMap(run -> runRepository.save(run));
  }

//  @Scheduled(cron = "* * * * * *")
  public void readyToRun() {
    // makes all scheduled runs to RUN
    //
    // STEPS
    // 1. get all runs where the scheduledToRunAt is less than time.now
    // 2. update its startedAt and save to database
    // 3. if save failed, cancel execution (it means other pod has already taking care of it)
    // 4. if save success, then execute (fire and forget)
    // 5. at the end, update its job's lastRunAt
  }

  public static Long getNextRunSchedule(String cronExpression) {
    try {
      CronExpression cron = new CronExpression(cronExpression);
      Date now = new Date();
      return cron.getNextValidTimeAfter(now).getTime();
    } catch (ParseException e) {
      throw new RuntimeException("Invalid cron expression: " + e.getMessage(), e);
    }
  }
}
