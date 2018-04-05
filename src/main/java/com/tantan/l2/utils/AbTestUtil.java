package com.tantan.l2.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AbTestUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbTestUtil.class);

  private static final String PARAMETER_SEPARATOR = "#";
  private static final String KEY_VALUE_SEPARATOR = ":";
  public static Map<String, Double> parseModelFromAbTest(String abTestSetting, Map<String, Double> defaultModel) {
    String[] parameters = abTestSetting.split(PARAMETER_SEPARATOR);
    if (parameters == null) {
      return defaultModel;
    }
    Map<String, Double> model = new HashMap<>();
    try {
      for (int i = 0; i < parameters.length; i++) {
        String[] data = parameters[i].trim().split(KEY_VALUE_SEPARATOR);
        if (data == null || data.length != 2) {
          continue;
        }
        model.put(data[0], Double.parseDouble(data[1]));
      }
      LOGGER.info("The lix mode is " + model.toString());
    } catch (Exception e) {
      LOGGER.error("Failed to parse model from AB Test Setting", e);
      return defaultModel;
    }
    return model;
  }
}
