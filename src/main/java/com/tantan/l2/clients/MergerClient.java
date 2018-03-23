package com.tantan.l2.clients;

import com.tantan.l2.models.User;
import com.tantan.l2.models.Users;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class MergerClient {
    /**
     * This method will get a user from id
     * @param id - user id
     * @return
     */
    public Users getUsers(Long id, String limit, String search, String filter, String with) {
        RestTemplate restTemplate = new RestTemplate();
        //TODO: change the url to merger url and replace the value to return
        Users usersFromMerger = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", Users.class);

        User user = new User().setId(1L).setDistance(1).setLastactivity("none").setPopularity(22).setScore(3).setType("type");
        List<User> userList = new ArrayList<User>();
        userList.add(user);
        return new Users(userList);
    }
}
