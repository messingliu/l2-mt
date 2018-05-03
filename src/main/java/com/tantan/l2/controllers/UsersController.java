package com.tantan.l2.controllers;

import com.tantan.l2.models.Resp;
import com.tantan.l2.models.UserInfoResponse;
import com.tantan.l2.services.SuggestedUsers;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class UsersController {
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SuggestedUsers suggestedUsers;

  @RequestMapping("/users")
  public Resp suggestedUsers(@RequestParam(value="user_id") Long userId,
                       @RequestParam(value="limit", defaultValue = "25") Integer limit,
                       @RequestParam(value="search") String search,
                       @RequestParam(value="filter", defaultValue = "") String filter,
                       @RequestParam(value="with") String with,
                       @RequestParam(value="byPassThroughMode", defaultValue = "true") boolean byPassThroughMode) throws ExecutionException, InterruptedException {
    Counter counter = Metrics.counter("received.request", "endpoint", "users");
    counter.increment();
    return suggestedUsers.getSuggestedUsers(userId, limit, search, filter, with, byPassThroughMode);
  }
}
