package com.tantan.l2.dao.hbase;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * HBase Data Access exception.
 *
 */
public class HbaseSystemException extends UncategorizedDataAccessException {

    public HbaseSystemException(Exception cause) {
        super(cause.getMessage(), cause);
    }

    public HbaseSystemException(Throwable throwable) {
        super(throwable.getMessage(), throwable);
    }
}
