package com.github.tianjing.tgtools.alibaba.log.client;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.common.QueriedLog;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.response.GetLogsResponse;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AliYunLogClient {
    //配置AccessKey、服务入口、Project名称、Logstore名称等相关信息。
    //阿里云访问密钥AccessKey。更多信息，请参见访问密钥。阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维。
    protected String accessKey;
    protected String accessSecret;
    //日志服务的服务入口。更多信息，请参见服务入口。
    //此处以杭州为例，其它地域请根据实际情况填写。
    protected String endpoint;
    //创建日志服务Client。
    protected Client client;
    protected String projectName;
    protected String logStore;
    public String getAccessKey() {
        return client.getAccessId();
    }

    public String getAccessSecret() {
        return client.getAccessKey();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void init(String pAccessSecret, String pAccessKey, String pEndpoint,String pProjectName, String pLogStoreName) {
        accessSecret = pAccessSecret;
        accessKey = pAccessKey;
        endpoint = pEndpoint;
        projectName = pProjectName;
        logStore = pLogStoreName;
        client = new Client(pEndpoint, accessKey, accessSecret);
    }

    protected void validClient() throws LogException {
        if (Objects.isNull(client)) {
            throw new LogException("-1", "请先使用 init 方法 初始化参数", "-1");
        }
    }

    /**
     * 外网环  境添加 50ms左右1条
     *
     * @param pLogItem
     * @param topic
     * @param source
     * @throws LogException
     */
    public void pushLogs(LogItem pLogItem, String topic, String source) throws LogException {
        validClient();
        client.PutLogs(projectName, logStore, topic, new ArrayList() {{
            add(pLogItem);
        }}, source);

    }

    /**
     *
     * 外网环境添加 50ms左右1条
     * @param pLogItemList
     * @param topic
     * @param source
     * @throws LogException
     */
    public void pushLogs(List<LogItem> pLogItemList, String topic, String source) throws LogException {
        validClient();
        client.PutLogs(projectName, logStore, topic, pLogItemList, source);

    }

    //通过SQL查询日志。
    public List<QueriedLog> queryLogs(int fromTime, int toTime, String topic, String query) throws LogException {
        validClient();
        GetLogsResponse getLogsResponse = client.GetLogs(projectName, logStore, fromTime, toTime, topic, query);
        return getLogsResponse.getLogs();
    }

}
