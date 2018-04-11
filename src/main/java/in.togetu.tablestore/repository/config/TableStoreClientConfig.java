package in.togetu.tablestore.repository.config;

import com.alicloud.openservices.tablestore.ClientConfiguration;
import com.alicloud.openservices.tablestore.writer.WriterConfig;
import in.togetu.tscommon.config.AliyunProductConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

public class TableStoreClientConfig {


    @Value("${tablestore.instance.name}")
    private String instanceName;

    @Value("${tablestore.instance.autoCreate:false}")
    private boolean autoCreate;

    @Autowired
    @Qualifier("tsConfig")
    private AliyunProductConfig tsConfig;

    private ClientConfiguration clientConfiguration = new ClientConfiguration();
    private WriterConfig writerConfig = new WriterConfig();
    private int columnMaxLength = 1048576;



    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public AliyunProductConfig getTsConfig() {
        return tsConfig;
    }

    public void setTsConfig(AliyunProductConfig tsConfig) {
        this.tsConfig = tsConfig;
    }

    public ClientConfiguration getClientConfiguration() {
        return clientConfiguration;
    }

    public void setClientConfiguration(ClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    public WriterConfig getWriterConfig() {
        return writerConfig;
    }

    public void setWriterConfig(WriterConfig writerConfig) {
        this.writerConfig = writerConfig;
    }

    public int getColumnMaxLength() {
        return columnMaxLength;
    }

    public void setColumnMaxLength(int columnMaxLength) {
        this.columnMaxLength = columnMaxLength;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }
}
