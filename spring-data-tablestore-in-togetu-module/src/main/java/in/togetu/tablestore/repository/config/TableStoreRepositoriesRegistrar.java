package in.togetu.tablestore.repository.config;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

public class TableStoreRepositoriesRegistrar  extends RepositoryBeanDefinitionRegistrarSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableTableStoreRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new TableStoreRepositoryConfigExtension();
    }
}
