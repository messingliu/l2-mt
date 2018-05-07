package com.tantan.l2.dao.hbase;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HbaseTemplate implements HbaseOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseTemplate.class);

    private Configuration configuration;

    private volatile Connection connection;

    public HbaseTemplate(Configuration configuration) {
        this.setConfiguration(configuration);
        Assert.notNull(configuration, " a valid configuration is required");
    }

    @Override
    public <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        StopWatch sw = new StopWatch();
        sw.start();
        Table table = null;
        try {
            table = this.getConnection().getTable(TableName.valueOf(tableName));
            return action.doInTable(table);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new HbaseSystemException(throwable);
        } finally {
            if (null != table) {
                try {
                    table.close();
                    sw.stop();
                } catch (IOException e) {
                    LOGGER.error("Failed in releasing hbase resource");
                }
            }
        }
    }

    @Override
    public <T> List<T> find(String tableName, String family, final RowMapper<T> action) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addFamily(Bytes.toBytes(family));
        return this.find(tableName, scan, action);
    }

    @Override
    public <T> List<T> find(String tableName, String family, String qualifier, final RowMapper<T> action) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return this.find(tableName, scan, action);
    }

    @Override
    public <T> List<T> find(String tableName, final Scan scan, final RowMapper<T> action) {
        return this.execute(tableName, new TableCallback<List<T>>() {
            @Override
            public List<T> doInTable(Table table) throws Throwable {
                int caching = scan.getCaching();
                if (caching == 1) {
                    scan.setCaching(5000);
                }
                ResultScanner scanner = table.getScanner(scan);
                try {
                    List<T> rs = new ArrayList<T>();
                    int rowNum = 0;
                    for (Result result : scanner) {
                        rs.add(action.mapRow(result, rowNum++));
                    }
                    return rs;
                } finally {
                    scanner.close();
                }
            }
        });
    }

    @Override
    public <T> T get(String tableName, String rowName, final RowMapper<T> mapper) {
        return this.get(tableName, rowName, null, null, mapper);
    }

    @Override
    public <T> T get(String tableName, String rowName, String familyName, final RowMapper<T> mapper) {
        return this.get(tableName, rowName, familyName, null, mapper);
    }

    @Override
    public <T> T get(String tableName, final String rowName, final String familyName, final String qualifier, final RowMapper<T> mapper) {
        try {
            return this.execute(tableName, new TableCallback<T>() {
                @Override
                public T doInTable(Table table) throws Throwable {
                    Get get = new Get(Bytes.toBytes(rowName));
                    if (StringUtils.isNotBlank(familyName)) {
                        byte[] family = Bytes.toBytes(familyName);
                        if (StringUtils.isNotBlank(qualifier)) {
                            get.addColumn(family, Bytes.toBytes(qualifier));
                        } else {
                            get.addFamily(family);
                        }
                    }
                    Result result = table.get(get);
                    return mapper.mapRow(result, 0);
                }
            });
        } catch (Throwable e) {
            LOGGER.error("HBase get fail", e);
            return null;
        }
    }

    @Override
    public <T> List<T> batchGet(List<String> rowIds, String table, String family, String qualifier, RowMapper<T> mapper) {
        try {
            final List<Get> gets = Lists.newArrayList();
            for (String rowId : rowIds) {
                Get get =  new Get(Bytes.toBytes(rowId));
                if (StringUtils.isNotBlank(family)) {
                    byte[] familyByte = Bytes.toBytes(family);
                    if (StringUtils.isNotBlank(qualifier)) {
                        get.addColumn(familyByte, Bytes.toBytes(qualifier));
                    } else {
                        get.addFamily(familyByte);
                    }
                }
                gets.add(get);
            }

            return this.execute(table, new TableCallback<List<T>>() {
                @Override
                public List<T> doInTable(Table table) throws Throwable {
                    final Result[] results = new Result[gets.size()];
                    long start = System.currentTimeMillis();
                    table.batch(gets, results);
                    long end = System.currentTimeMillis();
                    List<T> resultList = Lists.newArrayList();
                    int size = 0;
                    for (Result result : results) {
                        resultList.add(mapper.mapRow(result, 0));
                        size += getRowSize(result, family);
                    }
                    long end2 = System.currentTimeMillis();
                    LOGGER.info("[LogType: client] [ClientName: hbase-rawTime] [ResponseTime: {}][DataSize: {}]", end - start, size);
                    return resultList;
                }
            });
        } catch (Throwable e) {
            LOGGER.error("HBase batch get fail", e);
            return null;
        }
    }

    private int getRowSize(Result result, String family) {
        if (result == null) {
            return 0;
        }
        NavigableMap<byte[], byte[]> map = result.getFamilyMap(Bytes.toBytes(family));
        if (map == null) {
            return 0;
        }
        int size = 0;
        for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
            size += entry.getKey().length;
            size += entry.getValue().length;
        }

        return size;
    }

    @Override
    public void execute(String tableName, MutatorCallback action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");

        StopWatch sw = new StopWatch();
        sw.start();
        BufferedMutator mutator = null;
        try {
            BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
            mutator = this.getConnection().getBufferedMutator(mutatorParams.writeBufferSize(3 * 1024 * 1024));
            action.doInMutator(mutator);
        } catch (Throwable throwable) {
            sw.stop();
            throw new HbaseSystemException(throwable);
        } finally {
            if (null != mutator) {
                try {
                    mutator.flush();
                    mutator.close();
                    sw.stop();
                } catch (IOException e) {
                    LOGGER.error("Failed in releasing hbase mutator");
                }
            }
        }
    }

    @Override
    public void saveOrUpdate(String tableName, final Mutation mutation) {
        this.execute(tableName, new MutatorCallback() {
            @Override
            public void doInMutator(BufferedMutator mutator) throws Throwable {
                mutator.mutate(mutation);
            }
        });
    }

    @Override
    public void saveOrUpdates(String tableName, final List<Mutation> mutations) {
        this.execute(tableName, new MutatorCallback() {
            @Override
            public void doInMutator(BufferedMutator mutator) throws Throwable {
                mutator.mutate(mutations);
            }
        });
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        if (null == this.connection) {
            synchronized (this) {
                if (null == this.connection) {
                    try {
                        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(200, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
                        // init pool
                        poolExecutor.prestartCoreThread();
                        this.connection = ConnectionFactory.createConnection(configuration, poolExecutor);
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error("Failed in creating hbase connection");
                    }
                }
            }
        }
        return this.connection;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

}
