package com.tantan.l2.services;

import com.tantan.l2.clients.UserInfoClient;
import com.tantan.l2.models.UserInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

  @Autowired
  UserInfoClient _userInfoClient;
  @Override
  public UserInfoResponse getUserInfoResponse(long id, String type) {
    return _userInfoClient.getUsers(id, type);
  }
}
