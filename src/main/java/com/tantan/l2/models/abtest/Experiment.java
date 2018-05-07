package com.tantan.l2.models.abtest;

import java.util.List;

public class Experiment {
  private String model_target;
  private String experiment_name;
  private long experiment_id;
  private long hash_id;
  private List<Whitelist> whitelists;
  private List<Ramp> ramp;

  public String getModel_target() {
    return model_target;
  }

  public void setModel_target(String model_target) {
    this.model_target = model_target;
  }

  public String getExperiment_name() {
    return experiment_name;
  }

  public void setExperiment_name(String experiment_name) {
    this.experiment_name = experiment_name;
  }

  public long getExperiment_id() {
    return experiment_id;
  }

  public void setExperiment_id(long experiment_id) {
    this.experiment_id = experiment_id;
  }

  public long getHash_id() {
    return hash_id;
  }

  public void setHash_id(long hash_id) {
    this.hash_id = hash_id;
  }

  public List<Whitelist> getWhitelists() {
    return whitelists;
  }

  public void setWhitelists(List<Whitelist> whitelists) {
    this.whitelists = whitelists;
  }

  public List<Ramp> getRamp() {
    return ramp;
  }

  public void setRamp(List<Ramp> ramp) {
    this.ramp = ramp;
  }

  public static class Whitelist {
    private List<Long> user_ids;
    private String treatment;

    public List<Long> getUser_ids() {
      return user_ids;
    }

    public void setUser_ids(List<Long> user_ids) {
      this.user_ids = user_ids;
    }

    public String getTreatment() {
      return treatment;
    }

    public void setTreatment(String treatment) {
      this.treatment = treatment;
    }
  }

  public static class Ramp {
    private float percentage;
    private String treatment;

    public float getPercentage() {
      return percentage;
    }

    public void setPercentage(float percentage) {
      this.percentage = percentage;
    }

    public String getTreatment() {
      return treatment;
    }

    public void setTreatment(String treatment) {
      this.treatment = treatment;
    }
  }

}
