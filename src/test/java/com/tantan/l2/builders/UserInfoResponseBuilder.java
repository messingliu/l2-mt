package com.tantan.l2.builders;

import com.tantan.l2.models.UserInfoResponse;

import java.util.HashMap;
import java.util.Map;

public class UserInfoResponseBuilder {
  public UserInfoResponse build() {
    Map<String, String> record = new HashMap<String, String>();
    record.put("REC_ID", "REC_ID1");
    record.put("id", "1");
    UserInfoResponse res = new UserInfoResponse();
    res.setType("ALL");
    res.setReason("reason");
    res.setStatus(1);
    res.setRecord(record);
    return res;
  }
}
