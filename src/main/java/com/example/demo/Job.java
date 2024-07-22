package com.example.demo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.support.CronExpression;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = Job.COLLECTION)
public class Job {
  public static final String COLLECTION = "jobs";

  @Id
  private String id;
  private String name;
  private String description;
  private CronExpression cronExpression;
  private Integer misfire;

  private String endpoint;
  private Map<String, String> headers;
  private HttpMethod method;
  private String body;
}
