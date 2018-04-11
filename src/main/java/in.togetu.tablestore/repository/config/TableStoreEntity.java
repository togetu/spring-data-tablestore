package in.togetu.tablestore.repository.config;


import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableStoreEntity {
    String name() default "";
}
