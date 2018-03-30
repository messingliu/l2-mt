package com.tantan.l2.services;

import com.tantan.l2.clients.MergerClient;
import com.tantan.l2.models.User;
import com.tantan.l2.models.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.tantan.avro.Test;

import java.util.ArrayList;
import java.util.List;

@Service
public class L2ServiceImpl implements L2Service{

    @Autowired
    public KafkaTemplate<Integer, Test> kafkaTemplate;

  /**
   * This method will get a user from id
   * @param id - user id
   * @return
   */
  @Override
  public Users getUsers(Long id, String limit, String search, String filter, String with) {
    MergerClient mergerClient = new MergerClient();
    Test myTest = new Test("testName", 20, "female");
    User user = new User().setId(2L).setDistance(1).setLastactivity(myTest.getName()).setPopularity(22).setScore(3).setType("type");
    sendTestKafkaEvent(myTest);
    return mergerClient.getUsers(id, limit, search, filter, with);
  }

  public void sendTestKafkaEvent(Test myTest) {
    kafkaTemplate.send("test", myTest);
  }
}
