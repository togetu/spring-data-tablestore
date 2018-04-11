package in.togetu.tablestore.repository.support;

import com.alicloud.openservices.tablestore.AsyncClient;
import com.alicloud.openservices.tablestore.AsyncClientInterface;
import in.togetu.tablestore.repository.TableStoreRepository;
import in.togetu.tablestore.repository.config.TableStoreClientConfig;
import in.togetu.tscommon.config.AliyunProductConfig;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class TableStoreRepositoryFactory extends RepositoryFactorySupport {

    private final TableStoreClientConfig clientConfig;

    private final MappingContext mappingContext;

    public TableStoreRepositoryFactory(TableStoreClientConfig clientConfiguration, MappingContext mappingContext) {
        this.clientConfig = clientConfiguration;
        this.mappingContext = mappingContext;
    }


    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return new TableStoreEntityInformation(mappingContext.getPersistentEntity(domainClass));
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation metadata) {
        AliyunProductConfig tsConfig = clientConfig.getTsConfig();
        AsyncClientInterface asyncClient = new AsyncClient(tsConfig.getEndpoint(), tsConfig.getAccessKey(),
                tsConfig.getAccessSecret(), clientConfig.getInstanceName(), clientConfig.getClientConfiguration());
        // TODO Writer
        TableStoreClient client = new TableStoreClient(asyncClient, null, getEntityInformation(metadata.getDomainType()));
        TableStoreMappingContext storeMappingContext = (TableStoreMappingContext) mappingContext;
        storeMappingContext.setClient(client);
        TableStoreRepository tableStoreRepository = getTargetRepositoryViaReflection(metadata, client,
                 metadata.getDomainType().getName());
        return tableStoreRepository;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return TableStoreRepository.class;
    }
}
