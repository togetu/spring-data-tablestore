package in.togetu.tablestore.repository.support;

import com.alicloud.openservices.tablestore.model.ColumnType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import in.togetu.tablestore.repository.bean.Key;
import in.togetu.tablestore.repository.config.TableStoreEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class TableStoreEntityInformation<T, ID extends Key> extends PersistentEntityInformation<T, ID> {
    private static Logger LOGGER = LoggerFactory.getLogger(TableStoreEntityInformation.class);
    private final PersistentEntity entity;

    private final String entityName;

    // entity字段名：表字段名
    private BiMap<String, String> columnNames;
    // 主键字段名：字段类型
    private Map<String, ColumnType> keyColumns;
    // 属性字段名：字段类型
    private Map<String, ColumnType> columns;

    /**
     * @param entity must not be {@literal null}.
     */
    public TableStoreEntityInformation(PersistentEntity entity) {
        super(entity);
        this.entity = entity;
        TableStoreEntity tableStoreEntity = AnnotatedElementUtils.findMergedAnnotation(this.entity.getType(), TableStoreEntity.class);
        entityName = null != tableStoreEntity && StringUtils.hasText(tableStoreEntity.name()) ?
                tableStoreEntity.name() : entity.getType().getSimpleName();
        initTableStoreColumn((TableStorePersistentEntity) entity);

    }

    public PersistentEntity getEntity() {
        return entity;
    }

    public String getEntityName() {
        return entityName;
    }

    public Map<String, ColumnType> getKeyColumns() {
        return keyColumns;
    }


    public BiMap<String, String> getColumnNames() {
        return columnNames;
    }

    public Map<String, ColumnType> getColumns() {
        return columns;
    }


    private void initTableStoreColumn(TableStorePersistentEntity entity) {
        //TypeInformation typeInformation = entity.getTypeInformation();
        keyColumns = new LinkedHashMap<>();
        columns = new LinkedHashMap<>();
        columnNames = HashBiMap.create();
        List<TableStorePersistentProperty> propertyList = Lists.newArrayList(entity);
        Collections.sort(propertyList, new OrderComparator());
        LOGGER.debug("sorted properties: " + propertyList);

        propertyList.forEach(p -> {
            LOGGER.debug("property: {}", p);
            if (p.isPrimaryIdProperty()) {
                columnNames.put(p.getName(), p.getPrimaryId().name());
                keyColumns.put(p.getName(), getColumnsType(p.getType()));
            } else  {
                columns.put(p.getName(), getColumnsType(p.getType()));
            }
        });

    }

    private ColumnType getColumnsType(Class type) {
        if (String.class.equals(type)) {
            return ColumnType.STRING;
        } else if (Long.class.equals(type) || Integer.class.equals(type)) {
            return ColumnType.INTEGER;
        } else if (Boolean.class.equals(type)) {
            return ColumnType.BOOLEAN;
        } else if (Double.class.equals(type)) {
            return ColumnType.DOUBLE;
        } else if (Byte.class.equals(type)) {
            return ColumnType.BINARY;
        } else {
            throw new IllegalArgumentException("not found column type");
        }
    }

}
