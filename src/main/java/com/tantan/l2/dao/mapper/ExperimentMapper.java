package com.tantan.l2.dao.mapper;

import com.alibaba.fastjson.JSON;
import com.tantan.l2.dao.hbase.RowMapper;
import com.tantan.l2.models.abtest.Experiment;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class ExperimentMapper implements RowMapper<Experiment> {
  public static final String TABLE_NAME = "treatment_store";
  public static final String FAMILY_NAME = "f";
  private static final byte[] CF = FAMILY_NAME.getBytes();
  private static final byte[] CONTENT = "content".getBytes();

  @Override
  public Experiment mapRow(Result result, int rowNum) throws Exception {
    String content = Bytes.toString(result.getValue(CF, CONTENT));
    return JSON.parseObject(content, Experiment.class);
  }
}
