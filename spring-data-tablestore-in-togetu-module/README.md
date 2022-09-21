原作者
https://github.com/togetu/spring-data-tablestore

主要是为了发布到MAVEN里所以继承。以后实际使用后可能二次开发。

# spring-data-tablestore

spring-data-xxx for aliyun(alibaba clound) tablestore(hbase like)

```
 <dependency>
    <groupId>com.github.tianjing</groupId>
    <artifactId>spring-data-tablestore-in-togetu-module</artifactId>
    <version>2.3.4.1-SNAPSHOT</version>
 </dependency>    
```

# 1. build a entity looks like this

```
@Data
@TableStoreEntity(name = "comments")
public class CommentsEntity {

    @Id
    @PrimaryId(order = 1, name = "id", partition = true)
    private String id;
    @PrimaryId(order = 3, name = "user")
    private String user;
    private String content;
    private Long votes;
    @Column(name = "status")
    private String status;
    @PrimaryId(order = 2, name = "publishAt")
    private Long publishAt;

}
```

# 2 create CRUD, the same as spring-data framework will do

```
public interface CommentsRepository extends BaseRepository<CommentsEntity, Key> {
}
```

# 3 login the alibaba clound page to create tablestore database and table. remember endpoint, key and secret.

# 4 little config work to connect to tablestore service

```
@Configuration
@EnableTableStoreRepositories(basePackages = "xxxxx.repository")
public class ServiceConfig {

}


@TgToolsEnableTableStore
public class WebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);
        LogHelper.info("", "==================================================", "main");
    }

}
```

in application.yml(or properties,change the fomart)

```
tgtools.alibaba.tablestore.endpoint: 
tgtools.alibaba.tablestore.accessKey: 
tgtools.alibaba.tablestore.accessSecret: 
tgtools.alibaba.tablestore.instance.name: 

```

