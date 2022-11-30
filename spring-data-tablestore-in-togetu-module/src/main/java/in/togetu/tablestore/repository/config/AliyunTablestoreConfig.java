package in.togetu.tablestore.repository.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author
 */
@ConfigurationProperties(prefix = "tgtools.alibaba.tablestore")
public class AliyunTablestoreConfig {

    private String endpoint;
    private String accessKey;
    private String accessSecret;


    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }
}
