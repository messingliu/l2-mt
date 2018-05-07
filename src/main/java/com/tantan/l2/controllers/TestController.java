package com.tantan.l2.controllers;

import com.tantan.l2.dao.ExperimentDao;
import com.tantan.l2.dao.UserMetaInfoDao;
import com.tantan.l2.models.abtest.Experiment;
import com.tantan.l2.models.abtest.UserMetaInfo;
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
  @Autowired
  private UserMetaInfoDao userMetaInfoDao;
  @Autowired
  private ExperimentDao experimentDao;

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

  @RequestMapping("getUserMeta")
  @ResponseBody
  public UserMetaInfo getUserMeta(long userId) {
    try {
      UserMetaInfo userInfo = userMetaInfoDao.getUserMetaInfo(userId);
      return userInfo;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @RequestMapping("getExperiment")
  @ResponseBody
  public Experiment getExperiment(String rowKey) {
    try {
      return experimentDao.getExperiment(rowKey);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
