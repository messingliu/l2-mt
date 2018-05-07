package com.tantan.l2.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tantan.l2.constants.AbTestKeys;
import com.tantan.l2.constants.LogConstants;
import com.tantan.l2.models.*;
import com.tantan.l2.utils.JacksonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class RankerClient {
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
  private final boolean callRPC = true;
  /**
   * This method will get a user from id
   *
   * @param id - user id
   * @return
   */
//  private final static String url_link =
//      "http://localhost:8008/ranker?id=1&candidateIds=8,2,3,4&modelId=0&linearModelParameter=popularity:0!type:0.5!distance:0.5";
  private final static String[] url_link = {
          "http://10.189.100.34:8008/ranker",
          "http://10.189.100.35:8008/ranker",
          "http://10.189.100.36:8008/ranker",
          "http://10.189.100.40:8008/ranker",
          "http://10.189.100.41:8008/ranker",
          "http://10.189.100.34:8007/ranker",
          "http://10.189.100.35:8007/ranker",
          "http://10.189.100.36:8007/ranker",
          "http://10.189.100.40:8007/ranker",
          "http://10.189.100.41:8007/ranker"
  };

  private final static String[] rpc_url_link = {
          "http://10.189.100.34",
          "http://10.189.100.35",
          "http://10.189.100.36",
          "http://10.189.100.40",
          "http://10.189.100.41",
          "http://10.189.100.34",
          "http://10.189.100.35",
          "http://10.189.100.36",
          "http://10.189.100.40",
          "http://10.189.100.41"
  };
  private final static int[] rpc_url_port = {
          6565,
          6565,
          6565,
          6565,
          6565,
          6566,
          6566,
          6566,
          6566,
          6566
  };


  @Async
  public List<User> getRankerList(Long id, List<User> inputUserList, String linearModelParameter, int rankerId) {
    if (callRPC) {
      return getRankerListRpc(id, inputUserList, linearModelParameter, rankerId);
    }
    // URI (URL) parameters
    Map<String, Object> uriParams = new HashMap<>();
    List<Long> candidateIds = new ArrayList<>();
    Map<Long, User> userMap = new HashMap<>();
    for (User user : inputUserList) {
      candidateIds.add(user.getId());
      userMap.put(user.getId(), user);
    }
    String listOfIds = candidateIds.stream().map(Object::toString).collect(Collectors.joining(","));
    // Query parameters
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url_link[rankerId])
                                       .queryParam("candidateIds", listOfIds)
                                       .queryParam("id", id)
                                       .queryParam("modelId", 0)
                                       .queryParam("linearModelParameter", linearModelParameter)
                                       .queryParam("topK", 100);


    //Get from ranker
    String url = null;
    try {
      url = builder.buildAndExpand(uriParams).toUri().toURL().toString();
    } catch (MalformedURLException e) {
      LOGGER.error("Error in building url for ranking id " + id, e);
      e.printStackTrace();
    }

    long startTime = System.currentTimeMillis();
    //Get from merger
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getMessageConverters().add(new JacksonConverter());
    //convert json to java object
    ObjectMapper mapper = new ObjectMapper();
    String usersFromRanker = restTemplate.getForObject(url, String.class);
    List<UserFeatures> rankerFeatures = null;
    try {
      rankerFeatures = mapper.readValue(usersFromRanker, new TypeReference<List<UserFeatures>>() {});
    }catch (Exception e) {
      LOGGER.error("Error in getting features from ranker: " + url, e);
    }
    List<User> outputUserList = new ArrayList<>();
    for (UserFeatures userIdObject: rankerFeatures) {
      outputUserList.add(userMap.get(userIdObject.getId()));
    }
    long endTime = System.currentTimeMillis();
    LOGGER.info("[{}: {}][{}: {}][{}: {}]", LogConstants.LOGO_TYPE, LogConstants.CLIENT_CALL,
            LogConstants.CLIENT_NAME, LogConstants.RANKER, LogConstants.RESPONSE_TIME, endTime - startTime);
    return outputUserList;
  }

  public List<User> getRankerListRpc(Long id, List<User> inputUserList, String linearModelParameter, int rankerId) {
    List<Long> candidateIds = new ArrayList<>();
    Map<Long, User> userMap = new HashMap<>();
    for (User user : inputUserList) {
      candidateIds.add(user.getId());
      userMap.put(user.getId(), user);
    }
    long startTime = System.currentTimeMillis();

    RankerRpcClient rankerRpcClient = new RankerRpcClient(rpc_url_link[rankerId], rpc_url_port[rankerId]);
    List<UserFeatures> rankerFeatures = null;
    try {
      rankerFeatures = rankerRpcClient.getRankerList(id, candidateIds, linearModelParameter, rankerId);
    }catch (Exception e) {
      LOGGER.error("Error in getting features from ranker: ", e);
    }

    List<User> outputUserList = new ArrayList<>();
    for (UserFeatures userIdObject: rankerFeatures) {
      outputUserList.add(userMap.get(userIdObject.getId()));
    }
    long endTime = System.currentTimeMillis();
    LOGGER.info("[{}: {}][{}: {}][{}: {}]", LogConstants.LOGO_TYPE, LogConstants.CLIENT_CALL,
            LogConstants.CLIENT_NAME, LogConstants.RANKER, LogConstants.RESPONSE_TIME, endTime - startTime);
    return outputUserList;
  }
}
