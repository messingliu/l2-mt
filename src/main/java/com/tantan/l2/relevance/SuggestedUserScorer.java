package com.tantan.l2.relevance;

import com.tantan.l2.constants.AbTestKeys;
import com.tantan.l2.relevance.feature.Feature;
import com.tantan.l2.relevance.feature.FeatureVector;
import com.tantan.l2.utils.AbTestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SuggestedUserScorer {
  private static final Logger LOGGER = LoggerFactory.getLogger(SuggestedUserScorer.class);
  private static final Map<String, Double> DEFAULT_MODEL = new HashMap<>();
  private final Map<String, Double> _model;
  static {
    DEFAULT_MODEL.put(Feature.DISTANCE.name().toLowerCase(), 1.0);
    DEFAULT_MODEL.put(Feature.POPULARITY.name().toLowerCase(), 1.0);
    DEFAULT_MODEL.put(Feature.TYPE.name().toLowerCase(), 1.0);
  }

  public SuggestedUserScorer(Map<String, String> abTestKeyMap) {
    _model = AbTestUtil.parseModelFromAbTest(abTestKeyMap.get(AbTestKeys.SUGGESTED_USER_MODEL.name()), DEFAULT_MODEL);
    LOGGER.info("The AB test model is : " + _model);
  }

  public double score(FeatureVector<Double> v) {
    double score = 0.0;
    for (String feature : v.getIndexMap().keySet()) {
      score += (Double)v.getElementData()[v.getIndexMap().get(feature.toLowerCase())] *
          _model.getOrDefault(feature.toLowerCase(), 0.0);
    }
    return score;
  }
}
