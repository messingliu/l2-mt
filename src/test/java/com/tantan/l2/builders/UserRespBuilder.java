package com.tantan.l2.builders;

import com.tantan.l2.models.*;

import java.util.ArrayList;
import java.util.List;

public class UserRespBuilder {
  public Resp buildUserResp() {
    User user1 = new User().setId(1L).setDistance(1).setLastactivity("none").setPopularity(22).setScore(3).setType("type");
    User user2 = new User().setId(2L).setDistance(2).setLastactivity("none").setPopularity(33).setScore(4).setType("type");
    User user3 = new User().setId(3L).setDistance(3).setLastactivity("none").setPopularity(44).setScore(5).setType("type");
    List<User> userList = new ArrayList<User>();
    userList.add(user1);
    userList.add(user2);
    userList.add(user3);
    return new Resp().setMeta(new Meta(1L, "test")).setData(new UserList(userList)).setExtra(new Extra(false, 2));
  }
  public Resp buildRankedUserResp() {
    User user1 = new User().setId(1L).setDistance(1).setLastactivity("none").setPopularity(22).setScore(3).setType("type");
    List<User> userList = new ArrayList<User>();
    userList.add(user1);
    return new Resp().setMeta(new Meta(1L, "test")).setData(new UserList(userList)).setExtra(new Extra(false, 2));
  }
}
