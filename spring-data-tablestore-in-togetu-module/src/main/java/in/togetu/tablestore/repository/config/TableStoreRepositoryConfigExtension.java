package in.togetu.tablestore.repository.config;

import in.togetu.tablestore.repository.TableStoreRepository;
import in.togetu.tablestore.repository.support.TableStoreMappingContext;
import in.togetu.tablestore.repository.support.TableStoreRepositoryFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class TableStoreRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

    @Override
    public String getModuleName() {
        return "TableStore";
    }

    @Override
    protected String getModulePrefix() {
        return "tablestore";
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return TableStoreRepositoryFactoryBean.class.getName();
    }

    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Arrays.asList(TableStoreEntity.class);
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.<Class<?>>singleton(TableStoreRepository.class);
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, RepositoryConfigurationSource source) {
        super.postProcess(builder, source);
        builder.addPropertyReference("mappingContext", "tableStoreMappingContext");
    }

    @Override
    public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {
        super.registerBeansForRoot(registry, configurationSource);

        registerIfNotAlreadyRegistered(() -> new RootBeanDefinition(TableStoreMappingContext.class), registry,
                "tableStoreMappingContext", configurationSource);
    }
}
