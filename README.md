原作者
https://github.com/togetu/spring-data-tablestore

主要是为了发布到MAVEN里所以继承。以后实际使用后可能二次开发。

# spring-data-tablestore

spring-data-xxx for aliyun(alibaba clound) tablestore(hbase like)

clone, and run `mvnw install` to local. import to your project

```
 <dependency>
    <groupId>com.github.tianjing</groupId>
    <artifactId>spring-data-tablestore-in-togetu</artifactId>
    <version>0.0.1-SNAPSHOT</version>
 </dependency>    
```

# 1. build a entity looks like this

```
@TableStoreEntity(name = "comments")
public class CommentsEntity {

    @Id
    @PrimaryId(order = 1, name = "id", partition = true)
    private String id;
    @PrimaryId(order = 3, name = "user")
    private String user;
    private String content;
    private Long votes;
    private String status;
    @PrimaryId(order = 2, name = "publishAt")
    private Long publishAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getVotes() {
        return votes;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(Long publishAt) {
        this.publishAt = publishAt;
    }

    @Override
    public String toString() {
        return "CommentsEntity{" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", content='" + content + '\'' +
                ", votes=" + votes +
                ", status='" + status + '\'' +
                ", publishAt=" + publishAt +
                '}';
    }
}
```

# 2 create CRUD, the same as spring-data framework will do

```
public interface CommentsRepository extends PagingAndSortingRepository<CommentsEntity, Key> {
}
```

# 3 login the alibaba clound page to create tablestore database and table. remember endpoint, key and secret.

# 4 little config work to connect to tablestore service

```
@Configuration
@EnableTableStoreRepositories(basePackages = "com.package.to.your.repository")
public class ServiceConfig {

    @Bean("tsConfig")
    @ConfigurationProperties(prefix = "tablestore")
    public AliyunProductConfig getTableStoreConfig() {
        // manual fill the config if not use spring boot
        return new AliyunProductConfig();
    }
}
```

in application.yml(or properties,change the fomart)

```
tablestore:
  product: tablestore
  region: xxxx
  endpoint: https://xxxxx.xxxx.ots.aliyuncs.com
  accessKey: xxxxx
  accessSecret: xxxxxx
  instance:
    name: nameofinstace

```

# 5 enjoy it

