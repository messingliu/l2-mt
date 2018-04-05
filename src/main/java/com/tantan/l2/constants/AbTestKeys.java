package com.tantan.l2.constants;

public enum AbTestKeys {
  /**
   * this lix is used to choose the suggested user model
   */
  SUGGESTED_USER_MODEL("suggested-users-model");

  private String _key;
  AbTestKeys(String key) {_key = key;};
  String getKey() {
    return _key;
  }
}
