package com.example.demo.unit;


import com.example.demo.daemon.DaemonWorker;
import org.junit.jupiter.api.Test;

public class CronUnitTest {

  @Test
  void testConvertRegularCronIntoQuartzCron() {
    String regularCron = "* * * * * *";
    Long nextRunSchedule = DaemonWorker.getNextRunSchedule(regularCron);
    System.out.println(nextRunSchedule);
  }
}
