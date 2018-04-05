package com.tantan.l2.relevance;

import com.tantan.l2.clients.AbTestClient;
import com.tantan.l2.constants.AbTestKeys;
import com.tantan.l2.models.User;
import com.tantan.l2.relevance.feature.Feature;
import com.tantan.l2.relevance.feature.FeatureVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
public class SuggestedUserRanker {
  private static final Logger LOGGER = LoggerFactory.getLogger(SuggestedUserRanker.class);
  private static final Set<String> AB_TEST_KEYS = new HashSet<>();
  private static final float LARGE_DISTANCE = 10000000;
  @Autowired
  private AbTestClient _abTestClient;

  static {
    AB_TEST_KEYS.add(AbTestKeys.SUGGESTED_USER_MODEL.name());
  }
  public List<User> getSuggestedUsers(int userId, List<User> userList, int topK) {
    Map<String, String> abTestMap = _abTestClient.getTreatments(userId, AB_TEST_KEYS);
    SuggestedUserScorer suggestedUserScorer = new SuggestedUserScorer(abTestMap);
    for (User user : userList) {
      user.setScore((float)suggestedUserScorer.score(getFeatureVectorForUser(user)));
    }
    return rankSuggestedUsers(userList, topK);
  }

  private List<User> rankSuggestedUsers(List<User> userList, int topK) {
    if (userList == null) {
      return Collections.EMPTY_LIST;
    }
    Collections.sort(userList, new Comparator<User>() {
      @Override
      public int compare(User a, User b) {
        if (a.getScore() == b.getScore()) {
          return 0;
        } else if (a.getScore() < b.getScore()) {
          return 1;
        } else {
          return -1;
        }
      }
    });
    List<User> suggestedUserList = new ArrayList<>();
    for(User user: userList) {
      LOGGER.error("suggestedUserList " + user.getId() + " " + user.getScore());
    }
    for (int i = 0; i < Math.min(topK, userList.size()); i ++) {
      suggestedUserList.add(userList.get(i));
    }
    return suggestedUserList;
  }

  public FeatureVector<Double> getFeatureVectorForUser(User user) {
    Map<String, Integer> indexMap = new HashMap<>();
    indexMap.put(Feature.POPULARITY.name().toLowerCase(), 0);
    indexMap.put(Feature.TYPE.name().toLowerCase(), 1);
    indexMap.put(Feature.DISTANCE.name().toLowerCase(), 2);
    Double[] value = new Double[3];
    value[0] = new Double(user.getPopularity());
    value[1] = 0.5;
    value[2] = new Double(1 -  user.getDistance() / LARGE_DISTANCE);
    return new FeatureVector<>(value, indexMap);
  }
}
