package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(Run.COLLECTION)
public class Run {
  public static final String COLLECTION = "runs";

  @Id
  private String id;
  @Version
  private Integer v;

  private String jobId;
  private String status;

  private Long scheduledToRunAt;
  private Long startedAt;
  private Long expiredAt;
  private Long completedAt;
}
