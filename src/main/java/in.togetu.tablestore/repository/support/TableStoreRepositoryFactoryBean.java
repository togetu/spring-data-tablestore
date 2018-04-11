package in.togetu.tablestore.repository.support;

import in.togetu.tablestore.repository.config.TableStoreClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class TableStoreRepositoryFactoryBean<T extends Repository<S, ID>, S, ID> extends RepositoryFactoryBeanSupport<T, S, ID> {
    /**
     * Creates a new {@link RepositoryFactoryBeanSupport} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    protected TableStoreRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }


    @Autowired
    private TableStoreClientConfig clientConfiguration;


    private MappingContext mappingContext;

    public void setClientConfiguration(TableStoreClientConfig clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
    }

    @Override
    public void setMappingContext(MappingContext<?, ?> mappingContext) {
        super.setMappingContext(mappingContext);
        this.mappingContext = mappingContext;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory() {
        return new TableStoreRepositoryFactory(clientConfiguration, mappingContext);
    }
}
