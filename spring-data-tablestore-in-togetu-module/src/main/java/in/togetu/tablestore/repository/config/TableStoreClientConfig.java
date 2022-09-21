package in.togetu.tablestore.repository.config;

import com.alicloud.openservices.tablestore.ClientConfiguration;
import com.alicloud.openservices.tablestore.writer.WriterConfig;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
public class TableStoreClientConfig {


    @Value("${tgtools.alibaba.tablestore.instance.name}")
    private String instanceName;

    @Value("${tgtools.alibaba.tablestore.instance.autoCreate:false}")
    private boolean autoCreate;

    @Autowired
    @Qualifier("aliyunTablestoreConfig")
    private AliyunTablestoreConfig aliyunTablestoreConfig;

    private ClientConfiguration clientConfiguration = new ClientConfiguration();
    private WriterConfig writerConfig = new WriterConfig();
    private int columnMaxLength = 1048576;



}
