package com.example.demo.unit;


import com.example.demo.worker.SchedulerWorker;
import org.junit.jupiter.api.Test;

public class CronUnitTest {

  @Test
  void testConvertRegularCronIntoQuartzCron() {
    String regularCron = "* * * * * *";
    Long nextRunSchedule = SchedulerWorker.getNextRunSchedule(regularCron);
    System.out.println(nextRunSchedule);
  }
}
