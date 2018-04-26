package com.tantan.l2.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFeatures {

  @JsonProperty("id")
  private Long id;
  @JsonProperty("features")
  private List<Float> features;

  public UserFeatures() {
  }

  public UserFeatures(Long id, List<Float> features) {
    this.id = id;
    this.features = features;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setFeatures(List<Float> features) {
    this.features = features;
  }

  public List<Float> getFeatures() {
    return features;
  }
}
