package com.tantan.l2.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class L1User {
  private long id;
  private String rawData;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getRawData() {
    return rawData;
  }

  public void setRawData(String rawData) {
    this.rawData = rawData;
  }
}

