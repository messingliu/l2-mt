package com.tantan.l2.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Users {

    private List<User> users;

    public Users() {}

    public Users(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public Users setUsers(List<User> users) {
        this.users = users;
        return this;
    }
}
