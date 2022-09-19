package in.togetu.tablestore.repository.support;

import in.togetu.tablestore.repository.config.PrimaryId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import javax.persistence.Column;

public class TableStorePersistentProperty<P extends TableStorePersistentProperty<P>> extends AnnotationBasedPersistentProperty<P>
        implements Ordered {

    private PrimaryId primaryId;
    private Column column;

    /**
     * Creates a new {@link AnnotationBasedPersistentProperty}.
     *
     * @param property         must not be {@literal null}.
     * @param owner            must not be {@literal null}.
     * @param simpleTypeHolder
     */
    public TableStorePersistentProperty(Property property, PersistentEntity<?, P> owner, SimpleTypeHolder simpleTypeHolder) {
        super(property, owner, simpleTypeHolder);

        primaryId = (PrimaryId) findAnnotation(PrimaryId.class);
        column = (Column) findAnnotation(Column.class);
    }

    @Override
    protected Association<P> createAssociation() {
        return new Association<>((P) this, null);
    }

    @Override
    public boolean isIdProperty() {
        return super.isIdProperty();
    }

    public boolean isPrimaryIdProperty() {
        return primaryId != null;
    }

    public PrimaryId getPrimaryId() {
        return primaryId;
    }

    @Override
    public String getName() {
        if (null != column && StringUtils.isNotEmpty(column.name())) {
            return column.name();
        }
        return super.getName();
    }

    @Override
    public int getOrder() {
        if (isPrimaryIdProperty()) {
            return primaryId.order();
        } else {
            return 100;
        }
    }
}
