package com.tantan.l2.enums;

public enum ModelTarget {
  FEMALE_MLC0("tantan-rec-female-mlc0"),
  FEMALE_MLC1("tantan-rec-female-mlc1"),
  FEMALE_MLC2("tantan-rec-female-mlc2"),
  FEMALE_MLC4("tantan-rec-female-mlc4"),
  MALE_MLC0("tantan-rec-male-mlc0"),
  MALE_MLC1("tantan-rec-male-mlc1"),
  MALE_MLC2("tantan-rec-male-mlc2"),
  MALE_MLC4("tantan-rec-male-mlc4");

  private final String name;

  ModelTarget(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
