package com.tantan.l2.services;
import com.tantan.l2.models.Resp;


public interface SuggestedUsers {
  /**
   * This method will get a list of users from id
   * @param id - user id
   * @return
   */
  public Resp getSuggestedUsers(Long id, Integer limit, String search, String filter, String with);
}