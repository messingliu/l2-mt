package com.tantan.l2.clients;

import com.tantan.l2.models.UserInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserInfoClient {
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  public UserInfoResponse getUsers(Long id, String type) {
//    RestTemplate restTemplate = new RestTemplate();
//    String url = "http://10.191.161.72:9872/info?uid=" + id.toString() + "&type=" + type;
//    UserInfoResponse res = null;
//    try {
//      restTemplate.getForObject(url, UserInfoResponse.class);
//    } catch (Exception e) {
//      LOGGER.error("Error in getting response from UserInfo client", e);
//    }
    //TODO: mock here
    Map<String, String> record = new HashMap<String, String>();
    record.put("REC_ID", "REC_ID1");
    record.put("id", "1");
    UserInfoResponse res = new UserInfoResponse();
    res.setType("ALL");
    res.setReason("reason");
    res.setStatus(1);
    res.setRecord(record);
    return res;
  }
}
