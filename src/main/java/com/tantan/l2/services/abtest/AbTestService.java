package com.tantan.l2.services.abtest;

import com.tantan.l2.dao.UserMetaInfoDao;
import com.tantan.l2.models.abtest.Experiment;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbTestService {
  @Autowired
  private UserMetaInfoDao userMetaInfoDao;
  @Autowired
  private ModelTargetService modelTargetService;
  @Autowired
  private ExperimentService experimentService;

  public Experiment getExperiment(long userId) {
    return experimentService.getExperiment(userId);
  }

  public Pair<String, Integer> getTreatment(long userId, Experiment experiment) {
    return experimentService.getTreatment(userId, experiment);
  }



}
