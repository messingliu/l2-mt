package com.tantan.l2.services;

import com.tantan.l2.clients.MergerClient;
import com.tantan.l2.models.Resp;
import com.tantan.l2.models.User;
import com.tantan.l2.models.UserInfoResponse;
import com.tantan.l2.relevance.SuggestedUserRanker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.tantan.avro.KafkaTest;
import com.tantan.avro.AvroExtraTest;
//import com.tantan.avro.AvroDataTest;
import com.tantan.avro.AvroMetaTest;
import com.tantan.avro.AvroUsersTest;
import com.tantan.avro.AvroUserTest;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestedUsersImpl implements SuggestedUsers {

  @Autowired
  public KafkaTemplate<Integer, KafkaTest> _kafkaTemplate;

  @Autowired
  public MergerClient _mergerClient;

  @Autowired
  public UserInfoService _userInfoService;

  @Autowired
  private SuggestedUserRanker _suggestedUserRanker;

  /**
   * This method will get a user from id
   *
   * @param id - user id
   * @return
   */
  @Override
  public Resp getSuggestedUsers(Long id, String limit, String search, String filter, String with) {
    Resp mergerResult = _mergerClient.getUsers(id, limit, search, filter, with);
    UserInfoResponse userInfoResponse = _userInfoService.getUserInfoResponse(id, "ALL");
    List<User> topKUsers = _suggestedUserRanker.getSuggestedUsers(id, userInfoResponse, mergerResult.getData().getUsers(), 1);
    mergerResult.getData().setUsers(topKUsers);
    sendKafkaTestKafkaEvent(mergerResult);
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
