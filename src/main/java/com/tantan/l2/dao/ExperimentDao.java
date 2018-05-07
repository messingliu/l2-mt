package com.tantan.l2.dao;

import com.tantan.l2.dao.hbase.HbaseTemplate;
import com.tantan.l2.dao.mapper.ExperimentMapper;
import com.tantan.l2.models.abtest.Experiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExperimentDao {
  @Autowired
  private HbaseTemplate hbaseTemplate;

  public Experiment getExperiment(String rowKey) {
    return hbaseTemplate.get(ExperimentMapper.TABLE_NAME, rowKey, ExperimentMapper.FAMILY_NAME, new ExperimentMapper());
  }

}
