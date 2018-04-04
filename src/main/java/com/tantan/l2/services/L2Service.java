package com.tantan.l2.services;
import com.tantan.l2.models.Users;
import com.tantan.l2.models.Resp;


public interface L2Service {
  /**
   * This method will get a list of users from id
   * @param id - user id
   * @return
   */
  public Resp getUsers(Long id, String limit, String search, String filter, String with);
}