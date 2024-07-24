package com.example.demo.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveJobWebRequest {

  private String name;
  private String description;
  private String cronExpression;

  private String endpoint;
  private Map<String, String> headers;
  private String httpMethod;
  private String body;
}
