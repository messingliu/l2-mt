package com.tantan.l2.clients;

import com.tantan.l2.models.User;
import com.tantan.l2.models.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class MergerClient {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * This method will get a user from id
     * @param id - user id
     * @return
     */
    public Users getUsers(Long id, String limit, String search, String filter, String with) {
        RestTemplate restTemplate = new RestTemplate();
        //TODO: change the url to merger url and replace the value to return
        String usersFromMerger = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", String.class);
        LOGGER.info("usersFromMerger data is :  " + usersFromMerger.toString());

        User user = new User().setId(1L).setDistance(1).setLastactivity("none").setPopularity(22).setScore(3).setType("type");
        List<User> userList = new ArrayList<User>();
        userList.add(user);
        return new Users(userList);
    }
}
