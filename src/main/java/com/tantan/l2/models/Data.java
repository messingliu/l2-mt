package com.tantan.l2.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Data {

    @JsonProperty("data")
    private Users users;

    public Data() {}

    public Data(Users users) {
        this.users = users;
    }

    public Users getUsers() {
        return users;
    }

    public Data setUsers(Users users) {
        this.users = users;
        return this;
    }
}
