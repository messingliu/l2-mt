package com.tantan.l2.dao.mapper;

import com.tantan.l2.dao.hbase.RowMapper;
import com.tantan.l2.models.abtest.UserMetaInfo;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class UserMetaInfoMapper implements RowMapper<UserMetaInfo> {
    public static final String TABLE_NAME = "hbase_user_weekly_member_metadata";
    public static final String FAMILY_NAME = "f";
    private static final String ROW_KEY_PREFIX = "user_id_";
    private static final byte[] CF = FAMILY_NAME.getBytes();
    private static final byte[] AGE = "age".getBytes();
    private static final byte[] GENDER = "gender".getBytes();
    private static final byte[] MLC_WEEK0 = "mlc_week_0".getBytes();
    private static final byte[] MLC_WEEK1 = "mlc_week_1".getBytes();
    private static final byte[] MLC_WEEK2 = "mlc_week_2".getBytes();

    @Override
    public UserMetaInfo mapRow(Result result, int rowNum) {
        UserMetaInfo info = new UserMetaInfo();
        try {
            info.setUserId(getUserId(Bytes.toString(result.getRow())));
            info.setAge(NumberUtils.toInt(Bytes.toString(result.getValue(CF, AGE))));
            info.setMale(NumberUtils.toInt(Bytes.toString(result.getValue(CF, GENDER))) == 0); // TODO confirm
            info.setMlcWeek0(NumberUtils.toInt(Bytes.toString(result.getValue(CF, MLC_WEEK0))));
            info.setMlcWeek1(NumberUtils.toInt(Bytes.toString(result.getValue(CF, MLC_WEEK1))));
            info.setMlcWeek2(NumberUtils.toInt(Bytes.toString(result.getValue(CF, MLC_WEEK2))));
            return info;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRowKey(long userId) {
        return ROW_KEY_PREFIX + userId;
    }

    private long getUserId(String rowKey) {
        if (rowKey == null) {
            return -1;
        } else if (rowKey.startsWith(ROW_KEY_PREFIX)) {
            return NumberUtils.toLong(rowKey.substring(ROW_KEY_PREFIX.length(), -1));
        } else {
            return NumberUtils.toLong(rowKey, -1);
        }
    }

}
