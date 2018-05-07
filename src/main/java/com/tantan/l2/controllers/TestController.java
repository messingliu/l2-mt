package com.tantan.l2.controllers;

import com.tantan.l2.services.abtest.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {
  @Autowired
  private KafkaService kafkaService;

  @RequestMapping("sendKafka")
  @ResponseBody
  public String sendKafkaEvent(long userId) {
    try {
      kafkaService.sendABTestTreatmentEvent(userId);
      return userId + ": send kafka event OK";
    } catch (Exception e) {
       e.printStackTrace();
       return "FAIL";
    }
  }

}
