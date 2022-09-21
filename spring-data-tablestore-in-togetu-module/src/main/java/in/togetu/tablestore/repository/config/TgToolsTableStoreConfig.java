package in.togetu.tablestore.repository.config;

import org.springframework.context.annotation.Bean;

public class TgToolsTableStoreConfig {


    @Bean
    public AliyunTablestoreConfig aliyunTablestoreConfig() {
        return new AliyunTablestoreConfig();
    }


}
