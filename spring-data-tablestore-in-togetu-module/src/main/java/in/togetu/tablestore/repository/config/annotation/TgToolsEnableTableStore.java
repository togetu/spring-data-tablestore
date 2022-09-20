package in.togetu.tablestore.repository.config.annotation;

import in.togetu.tablestore.repository.config.TgToolsTableStoreConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@ImportAutoConfiguration(value = {TgToolsTableStoreConfig.class})
public @interface TgToolsEnableTableStore {
}
