package com.tantan.l2.services;

import com.tantan.l2.clients.AbTestClient;
import com.tantan.l2.clients.MergerClient;
import com.tantan.l2.clients.RankerClient;
import com.tantan.l2.constants.AbTestKeys;
import com.tantan.l2.constants.LogConstants;
import com.tantan.l2.models.Resp;
import com.tantan.l2.models.User;
import com.tantan.l2.models.UserInfoResponse;
import com.tantan.l2.relevance.SuggestedUserRanker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.tantan.avro.KafkaTest;
import com.tantan.avro.AvroExtraTest;
//import com.tantan.avro.AvroDataTest;
import com.tantan.avro.AvroMetaTest;
import com.tantan.avro.AvroUsersTest;
import com.tantan.avro.AvroUserTest;
import sun.rmi.runtime.Log;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class SuggestedUsersImpl implements SuggestedUsers {
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public KafkaTemplate<Integer, KafkaTest> _kafkaTemplate;

  @Autowired
  public MergerClient _mergerClient;

  @Autowired
  private RankerClient _rankerClient;

  @Autowired
  public UserInfoService _userInfoService;

  @Autowired
  private SuggestedUserRanker _suggestedUserRanker;

  @Autowired
  private AbTestClient _abTestClient;

  private static final Set<String> AB_TEST_KEYS = new HashSet<>();

  static {
    AB_TEST_KEYS.add(AbTestKeys.SUGGESTED_USER_MODEL.name());
  }

  /**
   * This method will get a user from id
   *
   * @param id - user id
   * @return
   */
  @Override
  @Async
  public CompletableFuture<Resp> getSuggestedUsers(Long id, Integer limit, String search, String filter, String with) {
    CompletableFuture<Resp> mergerResult = _mergerClient.getUsers(id, limit, search, filter, with);
    return mergerResult.thenCompose(result -> {
      UserInfoResponse userInfoResponse = _userInfoService.getUserInfoResponse(id, "ALL");
//    List<User> topKUsers = _suggestedUserRanker.getSuggestedUsers(id, userInfoResponse, mergerResult.getData().getUsers(), limit);
//    mergerResult.getData().setUsers(topKUsers);

      Map<String, String> abTestMap = _abTestClient.getTreatments(id, AB_TEST_KEYS);
      CompletableFuture<List<User>> suggestedUserList = _rankerClient.getRankerList(id, result.getData().getUsers(), abTestMap.get(AbTestKeys.SUGGESTED_USER_MODEL.name()));
      return suggestedUserList.thenApply(userList -> {
        result.getData().setUsers(userList);
        return result;
      });
    }).exceptionally(e -> {
      LOGGER.error("Error in getting suggested users for user ID: " + id, e);
      return null;
    });
    // sendKafkaTestKafkaEvent(mergerResult);
  }

  public void sendKafkaTestKafkaEvent(Resp mergerResult) {
    int rstSize = mergerResult.getData().getUsers().size();
    List<AvroUserTest> avrouser = new ArrayList<>();
    List<User> userList = mergerResult.getData().getUsers();
    for (int i = 0; i < rstSize; i++) {
      User currentUser = userList.get(i);
      long rst_id = currentUser.getId();
      float rst_score = currentUser.getScore();
      float rst_popularity = currentUser.getPopularity();
      float rst_distance = currentUser.getDistance();
      String rst_lastactivity = currentUser.getLastactivity();
      String rst_type = currentUser.getType();

      AvroUserTest myavrouser = AvroUserTest.newBuilder()
                                    .setId(rst_id)
                                    .setScore(rst_score)
                                    .setPopularity(rst_popularity)
                                    .setDistance(rst_distance)
                                    .setLastactivity(rst_lastactivity)
                                    .setType(rst_type)
                                    .build();
      avrouser.add(myavrouser);
    }
    AvroUsersTest myavrousers = AvroUsersTest.newBuilder().setUsers(avrouser).build();

    long rst_code = mergerResult.getMeta().getCode();
    String rst_msg = mergerResult.getMeta().getMessage();
    AvroMetaTest myavrometa = AvroMetaTest.newBuilder()
                                  .setCode(rst_code)
                                  .setMessage(rst_msg)
                                  .build();

    boolean rst_insufficient = mergerResult.getExtra().getIsInsufficient();
    long rst_selecetedcount = mergerResult.getExtra().getSelectedCount();
    AvroExtraTest myavroextra = AvroExtraTest.newBuilder()
                                    .setIsInsufficient(rst_insufficient)
                                    .setSelectedCount(rst_selecetedcount)
                                    .build();

    KafkaTest mykafkatest = KafkaTest.newBuilder()
                                .setMeta(myavrometa)
                                .setData(myavrousers)
                                .setExtra(myavroextra)
                                .build();

    _kafkaTemplate.send("test", mykafkatest);
  }
}
