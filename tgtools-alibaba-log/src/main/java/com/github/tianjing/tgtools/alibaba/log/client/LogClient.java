package com.github.tianjing.tgtools.alibaba.log.client;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetLogsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LogClient {
    //配置AccessKey、服务入口、Project名称、Logstore名称等相关信息。
    //阿里云访问密钥AccessKey。更多信息，请参见访问密钥。阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维。
    protected String accessId;
    protected String accessKey;
    //日志服务的服务入口。更多信息，请参见服务入口。
    //此处以杭州为例，其它地域请根据实际情况填写。
    protected String host;
    //创建日志服务Client。
    protected Client client;

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void init(String pAccessId, String pAccessKey, String pHost) {
        accessId = pAccessId;
        accessKey = pAccessKey;
        host = pHost;
        client = new Client(host, accessId, accessKey);
    }

    protected void validClient() throws Exception {
        if (Objects.isNull(client)) {
            throw new Exception("请先使用 init 方法 初始化参数");
        }
    }

    //向Logstore写入数据。
    //为了提高您系统的IO效率，请尽量不要直接使用该方式往日志服务中写数据，此方式仅为功能举例。
    //在大数据、高并发场景下建议使用Aliyun Log Java Producer方式写入日志数据。
    public void pushLogs(String projectName, String logStoreName, LogItem pLogItem, String topic, String source) throws LogException, InterruptedException {
        client.PutLogs(projectName, logStoreName, topic, new ArrayList() {{
            add(pLogItem);
        }}, source);
        TimeUnit.SECONDS.sleep(5);
    }

    //向Logstore写入数据。
    //为了提高您系统的IO效率，请尽量不要直接使用该方式往日志服务中写数据，此方式仅为功能举例。
    //在大数据、高并发场景下建议使用Aliyun Log Java Producer方式写入日志数据。
    public void pushLogs(String projectName, String logStoreName, List<LogItem> pLogItemList, String topic, String source) throws LogException, InterruptedException {
        client.PutLogs(projectName, logStoreName, topic, pLogItemList, source);
        TimeUnit.SECONDS.sleep(5);
    }

    //通过SQL查询日志。
    public List<QueriedLog> queryLogs(String projectName, String logStoreName, int fromTime, int toTime, String topic, String query) throws LogException {
        GetLogsResponse getLogsResponse = client.GetLogs(projectName, logStoreName, fromTime, toTime, topic, query);
        return getLogsResponse.getLogs();
    }
}
