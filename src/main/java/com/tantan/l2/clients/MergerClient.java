package com.tantan.l2.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tantan.l2.models.Resp;
import com.tantan.l2.models.User;
import com.tantan.l2.models.UserList;
import com.tantan.l2.models.Meta;
import com.tantan.l2.models.Extra;
import com.tantan.l2.utils.JacksonConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class MergerClient {
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
  private final boolean ableToCallMerger = false;
  /**
   * This method will get a user from id
   *
   * @param id - user id
   * @return
   */
  private final static String url_link = "http://127.0.0.1:8888/users?search=suggested,scenario-suggested" +
           "&filter=&with=contacts,questions,scenarios,user.publicMoments,relationships&user_id=";

  @Async
  public CompletableFuture<Resp> getUsers(Long id, int limit, String search, String filter, String with) {
    if (ableToCallMerger) {
      //Get from merger
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new JacksonConverter());
      String url = url_link + id + "&limit=" + limit;
      //convert json to java object
      ObjectMapper mapper = new ObjectMapper();
      String usersFromMerger = restTemplate.getForObject(url, String.class);
      Resp resp = null;
      try {
        resp = mapper.readValue(usersFromMerger, Resp.class);
      } catch (Exception e) {
        e.printStackTrace();
      }

      LOGGER.info("usersFromMerger data is :  " + usersFromMerger.toString());
      return CompletableFuture.completedFuture(resp);
    } else {

      //Test
      User user1 = new User().setId(1L).setDistance(1).setLastactivity("none").setPopularity(22).setScore(3).setType("type");
      User user2 = new User().setId(2L).setDistance(2).setLastactivity("none").setPopularity(33).setScore(4).setType("type");
      User user3 = new User().setId(3L).setDistance(3).setLastactivity("none").setPopularity(44).setScore(5).setType("type");

      List<User> userList = new ArrayList<User>();
      userList.add(user1);
      userList.add(user2);
      userList.add(user3);
      Resp resp = new Resp().setMeta(new Meta(1L, "test"))
                      .setData(new UserList(userList)).setExtra(new Extra(false, 2));
      return CompletableFuture.completedFuture(resp);
    }
  }
}
