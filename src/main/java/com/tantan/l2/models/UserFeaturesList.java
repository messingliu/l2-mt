package com.tantan.l2.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserFeaturesList {
  @JsonProperty("userFeaturesList")
  private List<UserFeatures> userFeaturesList;

  public UserFeaturesList(List<UserFeatures> userFeaturesList) {
    this.userFeaturesList = userFeaturesList;
  }

  public List<UserFeatures> getUserFeaturesList() {
    return userFeaturesList;
  }

  public void setUserFeaturesList(List<UserFeatures> userFeaturesList) {
    this.userFeaturesList = userFeaturesList;
  }
}
