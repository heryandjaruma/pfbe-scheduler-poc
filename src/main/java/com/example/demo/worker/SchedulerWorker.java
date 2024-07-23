package com.example.demo.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerWorker {

  @Scheduled(fixedDelay = 10000)
  public void readyToSchedule() {
    // check a job to be scheduled by creating its next run
    //
    // STEPS
    // 1. get all jobs where the lastRunAt AND lastScheduledAt are less than time.now
    // 2. create a new Run and save it to database
    // 3. update lastScheduledAt and save it to database
  }

  @Scheduled(fixedDelay = 100000)
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
}
