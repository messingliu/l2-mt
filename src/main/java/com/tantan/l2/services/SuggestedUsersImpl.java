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
import org.apache.avro.generic.GenericData;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
  private static final boolean callMultipleRanker = true;

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
  public Resp getSuggestedUsers(Long id, Integer limit, String search, String filter, String with, boolean byPassThroughMode) {
    return doGetSuggestUser(id, limit, search, filter, with, byPassThroughMode);
  }

  private Resp doGetSuggestUser(Long id, Integer limit, String search, String filter, String with, boolean byPassThroughMode) {
    Resp mergerResult;
    if (byPassThroughMode) {
      mergerResult = _mergerClient.getUsers(id, limit, search, filter, with);
    } else {
      mergerResult = _mergerClient.getUsersV2(id, limit, search, filter, with);
    }
    Map<String, String> abTestMap = _abTestClient.getTreatments(id, AB_TEST_KEYS);
    long startTime = System.currentTimeMillis();
    ExecutorService exs = Executors.newFixedThreadPool(15);
    List<User> suggestedUserList = new ArrayList<>();
    if (!callMultipleRanker) {
      List<User> mergerUsers = mergerResult.getData().getUsers();
      int oneListSize = Math.min(2000, mergerUsers.size() / 5);
      suggestedUserList = _rankerClient.getRankerList(id, mergerUsers.subList(0, oneListSize),
              abTestMap.get(AbTestKeys.SUGGESTED_USER_MODEL.name()), 3);
    } else {
      List<CompletableFuture<List<User>>> suggestedUserListFuture = new ArrayList<CompletableFuture<List<User>>>();
      List<User> mergerUsers = mergerResult.getData().getUsers();
      int oneListSize = Math.min(2000, mergerUsers.size() / 5);
      for (int i = 0; i < 10; i ++) {
        final int threadId = i;
        suggestedUserListFuture.add(i, CompletableFuture.supplyAsync(() -> {return _rankerClient.getRankerList(id, mergerUsers.subList(0, oneListSize),
                abTestMap.get(AbTestKeys.SUGGESTED_USER_MODEL.name()), threadId);}, exs));
      }
      List<List<User>> userTotalList = Stream.of(suggestedUserListFuture.get(0), suggestedUserListFuture.get(1), suggestedUserListFuture.get(2),
              suggestedUserListFuture.get(3), suggestedUserListFuture.get(4),suggestedUserListFuture.get(5), suggestedUserListFuture.get(6), suggestedUserListFuture.get(7),
              suggestedUserListFuture.get(8), suggestedUserListFuture.get(9)).map(CompletableFuture::join).collect(Collectors.toList());
      int total = 0;
      for (List<User> userList : userTotalList) {
        if (userList != null) {
          total += userList.size();
        }
      }
      LOGGER.info("[{}: {}][{}: {}][{}: {}]", LogConstants.LOGO_TYPE, LogConstants.CLIENT_CALL,
          LogConstants.CLIENT_NAME, LogConstants.RANKER_SIZE, LogConstants.DATA_SIZE, total);

      suggestedUserList = userTotalList.get(2);
    }
    mergerResult.getData().setUsers(suggestedUserList);
    exs.shutdown();
    long endTime = System.currentTimeMillis();
    LOGGER.info("[{}: {}][{}: {}][{}: {}]", LogConstants.LOGO_TYPE, LogConstants.CLIENT_CALL,
            LogConstants.CLIENT_NAME, LogConstants.RANKER_TOTAL, LogConstants.RESPONSE_TIME, endTime - startTime);
    return mergerResult;
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
