package com.tantan.l2.services.abtest;

import com.tantan.l2.constants.Constants;
import com.tantan.l2.dao.ExperimentDao;
import com.tantan.l2.dao.UserMetaInfoDao;
import com.tantan.l2.enums.ModelTarget;
import com.tantan.l2.models.abtest.Experiment;
import com.tantan.l2.models.abtest.UserMetaInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;

@Service
public class ExperimentService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentService.class);
  public static final String DEFAULT_TREATMENT = "legacy_model";
  @Autowired
  private UserMetaInfoDao userMetaInfoDao;
  @Autowired
  private ExperimentDao experimentDao;
  @Autowired
  private ModelTargetService modelTargetService;

  private static final byte[] MAX_MD5_BYTES = {(byte) 0xff, (byte)0xff,(byte)0xff,(byte)0xff, // TODO confirm max
      (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
      (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,
      (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
  private static final BigInteger TEN_K_DIVIDER = new BigInteger("1000");
  private static final BigInteger MAX_MD5 = new BigInteger(MAX_MD5_BYTES);

  public Experiment getExperiment(long userId) {
    UserMetaInfo userInfo = userMetaInfoDao.getUserMetaInfo(userId);
    ModelTarget modelTarget = modelTargetService.getModelTarget(! userInfo.isMale(), userInfo.getMlcWeek0());
    return experimentDao.getExperiment(getExperimentRowKey(modelTarget));
  }

  public Pair<String, Integer> getTreatment(long userId, Experiment experiment) {
    if (experiment != null) {
      if (CollectionUtils.isNotEmpty(experiment.getWhitelists())) {
        for (Experiment.Whitelist whitelist : experiment.getWhitelists()) {
          if (whitelist.getUser_ids() != null && whitelist.getUser_ids().contains(userId)) {
            return new Pair<>(whitelist.getTreatment(), 1);
          }
        }
      }
      if (CollectionUtils.isNotEmpty(experiment.getRamp())) {
        float percentage = getUserPercentage(userId, experiment.getHash_id());
        float accumulate = 0.0f;
        for (Experiment.Ramp ramp : experiment.getRamp()) {
          accumulate += ramp.getPercentage();
          if (percentage < accumulate) {
            return new Pair<>(ramp.getTreatment(), 2);
          }
        }
      }
    }

    return new Pair<>(DEFAULT_TREATMENT, 0);
  }

  private float getUserPercentage(long userId, long hashId) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(Constants.JOINER_COMMA.join(String.valueOf(userId), String.valueOf(hashId)).getBytes());
      byte[] digest = md5.digest();
      return new BigInteger(digest).multiply(TEN_K_DIVIDER).divide(MAX_MD5).divide(TEN_K_DIVIDER).floatValue();
    } catch (Exception e) {
      LOGGER.error("getUserPercentage Error", e);
      return 0.0f;
    }
  }

  public String getExperimentRowKey(ModelTarget modelTarget) {
    return modelTarget.getName();
  }
}
