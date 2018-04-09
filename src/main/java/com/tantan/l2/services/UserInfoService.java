package com.tantan.l2.services;

import com.tantan.l2.models.UserInfoResponse;

public interface UserInfoService {
  public UserInfoResponse getUserInfoResponse(long id, String type);
}
