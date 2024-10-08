package com.example.demo.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveRunWebRequest {
  private String jobId;
  private String status;

  private Long scheduledToRunAt;
  private Long completedAt;
}
