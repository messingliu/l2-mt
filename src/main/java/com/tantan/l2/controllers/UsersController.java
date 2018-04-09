package com.tantan.l2.controllers;

import com.tantan.l2.models.Resp;
import com.tantan.l2.models.UserInfoResponse;
import com.tantan.l2.services.SuggestedUsers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {
  @Autowired
  private SuggestedUsers suggestedUsers;

  @RequestMapping("/users")
  public Resp suggestedUsers(@RequestParam(value="id") Long id,
                       @RequestParam(value="limit") String limit,
                       @RequestParam(value="search") String search,
                       @RequestParam(value="filter", defaultValue = "") String filter,
                       @RequestParam(value="with") String with) {
    //User user = new User(counter.incrementAndGet(), 1, 2, 3, "here", "type");
    //SuggestedUsers suggestedUsers = new SuggestedUsersImpl();
    return suggestedUsers.getSuggestedUsers(id, limit, search, filter, with);
  }
}
