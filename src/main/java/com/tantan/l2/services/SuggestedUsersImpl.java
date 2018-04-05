package com.tantan.l2.services;

import com.tantan.l2.clients.MergerClient;
import com.tantan.l2.models.Resp;
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
  public KafkaTemplate<Integer, KafkaTest> kafkaTemplate;

  @Autowired
  public MergerClient _mergerClient;

  /**
   * This method will get a user from id
   *
   * @param id - user id
   * @return
   */
  @Override
  public Resp getSuggestedUsers(Long id, String limit, String search, String filter, String with) {
    Resp mergerResult = _mergerClient.getUsers(id, limit, search, filter, with);
    int rstSize = mergerResult.getData().getUsers().size();
    List<AvroUserTest> avrouser = new ArrayList<>();

    for (int i = 0; i < rstSize; i++) {
      long rst_id = mergerResult.getData().getUsers().get(i).getId();
      float rst_score = mergerResult.getData().getUsers().get(i).getScore();
      float rst_popularity = mergerResult.getData().getUsers().get(i).getPopularity();
      float rst_distance = mergerResult.getData().getUsers().get(i).getDistance();
      String rst_lastactivity = mergerResult.getData().getUsers().get(i).getLastactivity();
      String rst_type = mergerResult.getData().getUsers().get(i).getType();

      AvroUserTest myavrouser = AvroUserTest.newBuilder().setId(rst_id).setScore(rst_score).setPopularity(rst_popularity)
          .setDistance(rst_distance).setLastactivity(rst_lastactivity).setType(rst_type).build();

      avrouser.add(myavrouser);
    }
    AvroUsersTest myavrousers = AvroUsersTest.newBuilder().setUsers(avrouser).build();

    long rst_code = mergerResult.getMeta().getCode();
    String rst_msg = mergerResult.getMeta().getMessage();
    AvroMetaTest myavrometa = AvroMetaTest.newBuilder().setCode(rst_code).setMessage(rst_msg).build();

    boolean rst_insufficient = mergerResult.getExtra().getIsInsufficient();
    long rst_selecetedcount = mergerResult.getExtra().getSelectedCount();
    AvroExtraTest myavroextra = AvroExtraTest.newBuilder().setIsInsufficient(rst_insufficient).setSelectedCount(rst_selecetedcount).build();

    KafkaTest mykafkatest = KafkaTest.newBuilder().setMeta(myavrometa).setData(myavrousers).setExtra(myavroextra).build();

    sendKafkaTestKafkaEvent(mykafkatest);

    return mergerResult;
  }

  public void sendKafkaTestKafkaEvent(KafkaTest mykafkatest) {
    kafkaTemplate.send("test", mykafkatest);
  }
}
