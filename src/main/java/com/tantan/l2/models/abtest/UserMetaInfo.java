package com.tantan.l2.models.abtest;

public class UserMetaInfo {
  private long userId;
  private int age;
  private boolean male;
  private int mlcWeek0;
  private int mlcWeek1;
  private int mlcWeek2;

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public boolean isMale() {
    return male;
  }

  public void setMale(boolean male) {
    this.male = male;
  }

  public int getMlcWeek0() {
    return mlcWeek0;
  }

  public void setMlcWeek0(int mlcWeek0) {
    this.mlcWeek0 = mlcWeek0;
  }

  public int getMlcWeek1() {
    return mlcWeek1;
  }

  public void setMlcWeek1(int mlcWeek1) {
    this.mlcWeek1 = mlcWeek1;
  }

  public int getMlcWeek2() {
    return mlcWeek2;
  }

  public void setMlcWeek2(int mlcWeek2) {
    this.mlcWeek2 = mlcWeek2;
  }
}
