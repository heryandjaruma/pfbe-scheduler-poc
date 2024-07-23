package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

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
  @Version
  private Integer v;

  private String name;
  private String description;
  private String cronExpression;
  private Integer misfire;

  private String lastRunId;
  private Long lastRunAt;
  private Long lastScheduledAt;

  private String endpoint;
  private Map<String, String> headers;
  private String httpMethod;
  private String body;
}
