package com.tantan.l2.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserList {

  private List<User> users;

  public UserList() {
  }

  public UserList(List<User> users) {
    this.users = users;
  }

  public List<User> getUsers() {
    return users;
  }

  public UserList setUsers(List<User> users) {
    this.users = users;
    return this;
  }
}
