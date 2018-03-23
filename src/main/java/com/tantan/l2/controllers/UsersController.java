package com.tantan.l2.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.tantan.l2.models.User;
import com.tantan.l2.models.Users;
import com.tantan.l2.services.L2Service;
import com.tantan.l2.services.L2ServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/users")
    public Users greeting(@RequestParam(value="id") Long id,
                          @RequestParam(value="limit") String limit,
                          @RequestParam(value="search") String search,
                          @RequestParam(value="filter", defaultValue = "") String filter,
                          @RequestParam(value="with") String with) {
        //User user = new User(counter.incrementAndGet(), 1, 2, 3, "here", "type");
        L2Service l2Service = new L2ServiceImpl();
        return l2Service.getUsers(id, limit, search, filter, with);
    }
}
