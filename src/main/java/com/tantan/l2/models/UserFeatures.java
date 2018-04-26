package com.tantan.l2.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFeatures {

  private Long id;
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
