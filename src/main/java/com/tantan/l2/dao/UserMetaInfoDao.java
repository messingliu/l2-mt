package com.tantan.l2.dao;

import com.tantan.l2.dao.hbase.HbaseTemplate;
import com.tantan.l2.dao.mapper.UserMetaInfoMapper;
import com.tantan.l2.models.abtest.UserMetaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserMetaInfoDao {

  @Autowired
  private HbaseTemplate hbaseTemplate;

  public UserMetaInfo getUserMetaInfo(long userId) {
    return hbaseTemplate.get(UserMetaInfoMapper.TABLE_NAME, UserMetaInfoMapper.getRowKey(userId),
        UserMetaInfoMapper.FAMILY_NAME, new UserMetaInfoMapper());
  }

}
