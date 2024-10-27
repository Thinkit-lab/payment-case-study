package com.example.demo.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseEvent<T> {
  @JsonProperty("spec_version")
  private String specVersion;

  private String type;
  private String source;
  private String id;
  private String time;

  @JsonProperty("content_type")
  private String contentType = "application/json";

  private T data;
}
