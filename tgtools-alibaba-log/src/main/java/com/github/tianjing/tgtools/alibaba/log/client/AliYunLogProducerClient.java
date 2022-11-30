package com.github.tianjing.tgtools.alibaba.log.client;

import com.aliyun.openservices.aliyun.log.producer.*;
import com.aliyun.openservices.aliyun.log.producer.errors.ProducerException;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.exception.LogException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class AliYunLogProducerClient implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliYunLogProducerClient.class);

    //配置AccessKey、服务入口、Project名称、Logstore名称等相关信息。
    //阿里云访问密钥AccessKey。更多信息，请参见访问密钥。阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维。
    protected String accessKey;
    protected String accessSecret;
    //日志服务的服务入口。更多信息，请参见服务入口。
    //https://cn-guangzhou.log.aliyuncs.com
    protected String endpoint;
    protected String projectName;
    protected String logStore;

    private static final ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private Producer client;
    private AtomicLong completed = new AtomicLong(0);
    private CountDownLatch latch = new CountDownLatch(100);

    public void init(String pAccessSecret, String pAccessKey, String pEndpoint, String pProjectName, String pLogStore) {
        accessSecret = pAccessSecret;
        accessKey = pAccessKey;
        endpoint = pEndpoint;
        projectName = pProjectName;
        logStore = pLogStore;

        client = new LogProducer(new ProducerConfig());
        client.putProjectConfig(new ProjectConfig(projectName, endpoint, accessKey, accessSecret));
    }


    public void pushLogs(LogItem pLogItem, String topic, String source) {
        threadPool.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            client.send(
                                    projectName,
                                    logStore,
                                    topic,
                                    source,
                                    pLogItem,
                                    new SampleCallback(projectName, logStore, pLogItem, completed));
                        } catch (InterruptedException e) {
                            LOGGER.warn("The current thread has been interrupted during send logs.");
                        } catch (Exception e) {
                            LOGGER.error("Failed to send log, logItem={}, e=", pLogItem, e);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
    }

    public void waitToEnd() {
        try {
            while (true) {
                if (threadPool.getActiveCount() < 1) {
                    break;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            LOGGER.warn("The latch has been interrupted from close.");
        }
    }

    @Override
    public void close() {
        threadPool.shutdown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOGGER.warn("The latch has been interrupted from close.");
        }
        try {
            client.close();
        } catch (InterruptedException e) {
            LOGGER.warn("The current thread has been interrupted from close.");
        } catch (ProducerException e) {
            LOGGER.info("Failed to close producer, e=", e);
        }

        LOGGER.info("All log complete, completed={}", completed.get());
    }

    private static final class SampleCallback implements Callback {
        private static final Logger LOGGER = LoggerFactory.getLogger(SampleCallback.class);
        private final String project;
        private final String logStore;
        private final LogItem logItem;
        private final AtomicLong completed;

        SampleCallback(String project, String logStore, LogItem logItem, AtomicLong completed) {
            this.project = project;
            this.logStore = logStore;
            this.logItem = logItem;
            this.completed = completed;
        }

        @Override
        public void onCompletion(Result result) {
            try {
                if (result.isSuccessful()) {
                    LOGGER.info("Send log successfully.");
                } else {
                    LOGGER.error(
                            "Failed to send log, project={}, logStore={}, logItem={}, result={}",
                            project,
                            logStore,
                            logItem.ToJsonString(),
                            result);
                }
            } finally {
                completed.getAndIncrement();
            }
        }
    }

    public static void main(String[] args) throws LogException {
        String vAccessSecret = "Zt5QQRIa4sF8xGmRPw3cwrTdnlJbZc";
        String vAccessKey = "LTAI5tNhkZNuiSXBTHASGxFK";
        String vEndpoint = "https://cn-guangzhou.log.aliyuncs.com";
        AliYunLogProducerClient vAliYunLogClient = new AliYunLogProducerClient();
        vAliYunLogClient.init(vAccessSecret, vAccessKey, vEndpoint, "bodyup", "bodyup");
        long basetime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            LogItem vLogItem = new LogItem();
            vLogItem.PushBack("name", "wechat");
            vLogItem.PushBack("type", "server1");
            vLogItem.PushBack("input", "input" + i);
            vLogItem.PushBack("output", "output" + i);
            vAliYunLogClient.pushLogs(vLogItem, "", null);

        }
        vAliYunLogClient.waitToEnd();
        System.out.println(System.currentTimeMillis() - basetime);

        System.out.println("============================================================================");
    }
}
