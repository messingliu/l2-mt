package com.tantan.l2.clients;

import avro.shaded.com.google.common.collect.Lists;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tantan.l2.constants.LogConstants;
import com.tantan.l2.models.*;
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

  public Resp getUsers(Long id, int limit, String search, String filter, String with) {
    long startTime = System.currentTimeMillis();
    String url = null;
    if (ableToCallMerger) {
      url = url_link + id + "&limit=" + limit;
    } else {
      url = "http://10.189.100.43:8010/mockMerger?search=suggested,scenario-suggested&filter=&with=contacts," +
          "questions,scenarios,user.publicMoments,relationships" + "&user_id=" + id + "&limit=" + limit;
    }
    //Get from merger
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(new JacksonConverter());
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
    long endTime = System.currentTimeMillis();
    LOGGER.info("[{}: {}][{}: {}][{}: {}][{} : {}]", LogConstants.LOGO_TYPE, LogConstants.CLIENT_CALL,
            LogConstants.CLIENT_NAME, LogConstants.MERGER, LogConstants.RESPONSE_TIME, endTime - startTime,
            LogConstants.DATA_SIZE, resp.getData().getUsers().size());
    return resp;
  }


  public Resp getUsersV2(Long id, int limit, String search, String filter, String with) {
    Resp resp = new Resp();
    long startTime = System.currentTimeMillis();
    try {
      String url = "http://10.189.100.43:8010/mockMerger2?search=suggested,scenario-suggested&filter=&with=contacts," +
          "questions,scenarios,user.publicMoments,relationships" + "&user_id=" + id + "&limit=" + limit;
      //Get from merger
      RestTemplate restTemplate = new RestTemplate();
      restTemplate.getMessageConverters().add(new JacksonConverter());
      //convert json to java object
      ObjectMapper mapper = new ObjectMapper();
      String usersFromMerger = restTemplate.getForObject(url, String.class);
      List<L1User> l1users = mapper.readValue(usersFromMerger, new TypeReference<List<L1User>>() {
      });
      List<User> users = Lists.newArrayList();
      for (L1User l1user : l1users) {
        User user = new User();
        user.setId(l1user.getId());
        users.add(user);
      }
      resp.setData(new UserList(users));
    } catch (Exception e) {
      LOGGER.error("Merger client getUsersV2 fail", e);
    } finally {
      LOGGER.info("[{}: {}][{}: {}][{}: {}]", LogConstants.LOGO_TYPE, LogConstants.CLIENT_CALL,
          LogConstants.CLIENT_NAME, "Merger-getUsersV2", LogConstants.RESPONSE_TIME, System.currentTimeMillis() - startTime);
    }

    return resp;
  }


}
