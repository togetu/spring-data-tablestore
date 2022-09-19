package in.togetu.tablestore.repository;

import com.alicloud.openservices.tablestore.model.*;
import com.google.common.collect.BiMap;
import in.togetu.tablestore.repository.bean.Key;
import in.togetu.tablestore.repository.bean.KeyRange;
import in.togetu.tablestore.repository.bean.TableStorePage;
import in.togetu.tablestore.repository.bean.TableStorePageResult;
import in.togetu.tablestore.repository.support.TableStoreClient;
import in.togetu.tablestore.repository.support.TableStoreEntityInformation;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.Future;

public class TableStoreRepository<T, ID extends Key> implements BaseRepository<T, ID> {
    private static Logger LOGGER = LoggerFactory.getLogger(TableStoreRepository.class);

    private TableStoreClient client;
    private String tableName;

    public TableStoreRepository(TableStoreClient client, String tableName) {
        this.client = client;
        this.tableName = tableName;
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<T> findAll(Pageable  pageable) {
        Future<GetRangeResponse> range = client.getAsyncClient().getRange(createRangeRequest((TableStorePage)pageable), null);
        GetRangeResponse rangeResponse = Utils.waitForFuture(range);
        TableStorePageResult<T> pageResult = coverResponse(rangeResponse);
        return pageResult;
    }

    private TableStorePageResult<T> coverResponse(GetRangeResponse rangeResponse) {
        if (rangeResponse == null) return new TableStorePageResult<>(Collections.emptyList());

        List<Row> rows = rangeResponse.getRows();
        TableStoreEntityInformation entityInfomation = (TableStoreEntityInformation) client.getEntityInfomation();

        List<T> tList = new ArrayList<>(rows.size());
        for (Row row : rows) {
            T obj = getTObj(entityInfomation, row);
            tList.add(obj);
        }
        LOGGER.debug("found entity: {}", tList);
        TableStorePageResult<T> result = new TableStorePageResult<>(tList);
        if (rangeResponse.getNextStartPrimaryKey() != null) {
            PrimaryKeyColumn[] keys = rangeResponse.getNextStartPrimaryKey().getPrimaryKeyColumns();
            Key returnKey = new Key();

            for (PrimaryKeyColumn key : keys) {
                returnKey.put(key.getName(), asKeyType(key.getValue()));
            }
            result.setNextKey(returnKey);
        }
        return result;
    }

    /**
     * transfer row to entity
     * @param entityInfomation WYSIWYG
     * @param row WYSIWYG
     * @return T
     */
    private T getTObj(TableStoreEntityInformation entityInfomation,@NotNull Row row) {
        // 反射创建java对象
        T obj = (T) ReflectUtils.newInstance(entityInfomation.getJavaType());
        PersistentPropertyAccessor propertyAccessor = entityInfomation.getEntity().getPropertyAccessor(obj);
        // 设置对象主键值
        PrimaryKey primaryKey = row.getPrimaryKey();
        Map<String, PrimaryKeyColumn> nameMap = primaryKey.getPrimaryKeyColumnsMap();
        BiMap<String, String> columnNames = entityInfomation.getColumnNames().inverse();
        nameMap.forEach((name, value) -> {
            PersistentProperty property = entityInfomation.getEntity().getPersistentProperty(name);
            if (property == null) {
                property = entityInfomation.getEntity().getPersistentProperty(columnNames.get(name));
            }
            propertyAccessor.setProperty(property, asKeyType(value.getValue()));
        });
        // 设置对象普通属性值
        Arrays.asList(row.getColumns()).forEach(column -> {
            LOGGER.debug("cover column: {}", column);
            PersistentProperty property = entityInfomation.getEntity().getPersistentProperty(column.getName());
            if (property != null) {
                propertyAccessor.setProperty(property, asType(column.getValue()));
            }
        });
        return obj;
    }

    private Object asKeyType(PrimaryKeyValue value) {
        switch (value.getType()) {
            case STRING:
                return value.asString();
            case INTEGER:
                return value.asLong();
            case BINARY:
                return value.asBinary();
            default:
                throw new IllegalArgumentException("not found column type");
        }
    }

    private Object asType(ColumnValue value) {
        switch (value.getType()) {
            case STRING:
                return value.asString();
            case INTEGER:
                return value.asLong();
            case BOOLEAN:
                return value.asBoolean();
            case DOUBLE:
                return value.asDouble();
            case BINARY:
                return value.asBinary();
            default:
                throw new IllegalArgumentException("not found column type");
        }
    }

    private GetRangeRequest createRangeRequest(TableStorePage pageable) {
        TableStoreEntityInformation entityInfomation = (TableStoreEntityInformation) client.getEntityInfomation();

        Map<String, ColumnType> keyColumns = entityInfomation.getKeyColumns();

        RangeRowQueryCriteria rangeRowQueryCriteria = new RangeRowQueryCriteria(entityInfomation.getEntityName());
        final PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        KeyRange keyRange = pageable.getKeyRange();

        rangeRowQueryCriteria.setInclusiveStartPrimaryKey(buildPrimaryKey(keyRange.getFrom(), keyColumns, entityInfomation.getColumnNames()));
        // 设置结束主键
        rangeRowQueryCriteria.setExclusiveEndPrimaryKey(buildPrimaryKey(keyRange.getTo(), keyColumns, entityInfomation.getColumnNames()));
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            Sort.Order idOrder = sort.getOrderFor("id");
            boolean forward = idOrder == null || idOrder.getDirection() == Sort.Direction.ASC;
            rangeRowQueryCriteria.setDirection(forward ?  Direction.FORWARD : Direction.BACKWARD);
        } else {
            // default is forward
            rangeRowQueryCriteria.setDirection(Direction.FORWARD);
        }

        rangeRowQueryCriteria.setMaxVersions(1);
        rangeRowQueryCriteria.setLimit(pageable.getPageSize());
        return new GetRangeRequest(rangeRowQueryCriteria);
    }

    private PrimaryKey buildPrimaryKey(Key key, Map<String, ColumnType> keyColumns, BiMap<String, String> columnNames) {
        final PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
        key.getKeys().forEach((k,v) -> {
            ColumnType columnType = keyColumns.get(k);
            if (columnType == null) {
                columnType = keyColumns.get(columnNames.inverse().get(k));
            }
            if (columnType == null) throw new IllegalArgumentException("column not found");
            String columnName = columnNames.get(k);
            primaryKeyBuilder.addPrimaryKeyColumn(StringUtils.isEmpty(columnName) ? k: columnName, PrimaryKeyValue.fromColumn(new ColumnValue(v,columnType)));
        });

        return primaryKeyBuilder.build();
    }

    @Override
    public <S extends T> S save(S entity) {
        PutRowRequest rowRequest = createPutRequest(entity);
        Future<PutRowResponse> putRowResponseFuture = client.getAsyncClient().putRow(rowRequest, null);
        PutRowResponse putRowResponse = Utils.waitForFuture(putRowResponseFuture);
        PrimaryKey primaryKey = putRowResponse.getRow().getPrimaryKey();
        LOGGER.debug("put result key: {}", primaryKey);
        return entity;
    }

    private <S extends T> PutRowRequest createPutRequest(S entity) {
        TableStoreEntityInformation entityInfomation = (TableStoreEntityInformation) client.getEntityInfomation();
        Map<String, ColumnType> keyColumns = entityInfomation.getKeyColumns();

        PersistentPropertyAccessor propertyAccessor = entityInfomation.getEntity().getPropertyAccessor(entity);
        Key key = new Key();
        keyColumns.forEach((name, type) -> {
            Object value = propertyAccessor.getProperty(entityInfomation.getEntity().getPersistentProperty(name));
            if (value == null) {
                throw new IllegalArgumentException("keyColumns must not be null");
            }
            key.put(name, value);
        });
        RowPutChange rowPutChange = new RowPutChange(entityInfomation.getEntityName(), buildPrimaryKey(key, keyColumns, entityInfomation.getColumnNames()));
        Map<String, ColumnType> columns = entityInfomation.getColumns();
        columns.forEach((name, type) -> {
            Object value = propertyAccessor.getProperty(entityInfomation.getEntity().getPersistentProperty(name));
            if(value != null) {
                rowPutChange.addColumn(new Column(name, new ColumnValue(value, type)));
            }
        });
        rowPutChange.setReturnType(ReturnType.RT_PK);
        PutRowRequest rowRequest = new PutRowRequest(rowPutChange);
        LOGGER.debug("put row request: {}", ToStringBuilder.reflectionToString(rowPutChange, ToStringStyle.SHORT_PREFIX_STYLE));
        return rowRequest;
    }

    @Override
    public <S extends T> S update(S entity, List<String> attrs) {
        UpdateRowRequest updateRowRequest = createUpdateRequest(entity, attrs);
        Future<UpdateRowResponse> updateRowResponseFuture = client.getAsyncClient().updateRow(updateRowRequest, null);
        UpdateRowResponse updateRowResponse = Utils.waitForFuture(updateRowResponseFuture);
        return entity;
    }

    private <S extends T> UpdateRowRequest createUpdateRequest(S entity, List<String> attrs) {
        TableStoreEntityInformation entityInfomation = (TableStoreEntityInformation) client.getEntityInfomation();
        Map<String, ColumnType> keyColumns = entityInfomation.getKeyColumns();

        PersistentPropertyAccessor propertyAccessor = entityInfomation.getEntity().getPropertyAccessor(entity);
        Key key = new Key();
        keyColumns.forEach((name, type) -> {
            Object value = propertyAccessor.getProperty(entityInfomation.getEntity().getPersistentProperty(name));
            if (value == null) {
                throw new IllegalArgumentException("keyColumns must not be null");
            }
            key.put(name, value);
        });
        RowUpdateChange rowUpdateChange = new RowUpdateChange(entityInfomation.getEntityName(), buildPrimaryKey(key, keyColumns, entityInfomation.getColumnNames()));
        Map<String, ColumnType> columns = entityInfomation.getColumns();
        columns.forEach((name, type) -> {
            if(attrs.contains(name)) {
                Object value = propertyAccessor.getProperty(entityInfomation.getEntity().getPersistentProperty(name));
                if(value != null) {
                    rowUpdateChange.put(new Column(name, new ColumnValue(value, type)));
                } else {
                    rowUpdateChange.deleteColumns(name);
                }
            }
        });

        return new UpdateRowRequest(rowUpdateChange);
    }
    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<T> findById(ID id) {
        TableStoreEntityInformation entityInfomation = (TableStoreEntityInformation) client.getEntityInfomation();
        Map<String, ColumnType> keyColumns = entityInfomation.getKeyColumns();
        // params: tableName，pks
        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(entityInfomation.getEntityName(), buildPrimaryKey(id, keyColumns, entityInfomation.getColumnNames()));
        criteria.setMaxVersions(1);
        Future<GetRowResponse> getRowResponse = client.getAsyncClient().getRow(new GetRowRequest(criteria), null);
        GetRowResponse resp = Utils.waitForFuture(getRowResponse);
        if(resp == null || resp.getRow() == null) {
            return Optional.empty();
        }
        // result transfer : tablestore to entity
        T obj = getTObj(entityInfomation, resp.getRow());
        return Optional.of(obj);
    }

    @Override
    public boolean existsById(ID id) {
        return false;
    }

    @Override
    public Iterable<T> findAll() {
        LOGGER.info("find all");
        return null;
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        TableStoreEntityInformation entityInfomation = (TableStoreEntityInformation) client.getEntityInfomation();
        MultiRowQueryCriteria multiRowQueryCriteria = new MultiRowQueryCriteria(entityInfomation.getEntityName());
        ids.forEach((id) -> {
            multiRowQueryCriteria.addRow(buildPrimaryKey(id, entityInfomation.getKeyColumns(), entityInfomation.getColumnNames()));
        });
        multiRowQueryCriteria.setMaxVersions(1);
        BatchGetRowRequest batchGetRowRequest = new BatchGetRowRequest();
        // batchGetRow支持读取多个表的数据, 一个multiRowQueryCriteria对应一个表的查询条件, 可以添加多个multiRowQueryCriteria.
        batchGetRowRequest.addMultiRowQueryCriteria(multiRowQueryCriteria);
        Future<BatchGetRowResponse> batchGetRowResponseFuture = client.getAsyncClient().batchGetRow(batchGetRowRequest, null);
        BatchGetRowResponse batchGetRowResponse = Utils.waitForFuture(batchGetRowResponseFuture);
        LOGGER.debug("findAllById iterator isAllSucc is {}, failedRows is {}", batchGetRowResponse != null && batchGetRowResponse.isAllSucceed(), batchGetRowResponse.getFailedRows());
        List<T> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(batchGetRowResponse.getSucceedRows())) {
            batchGetRowResponse.getSucceedRows().forEach(rowResult -> {
                if(rowResult.getRow() != null) {
                    T obj = getTObj(entityInfomation, rowResult.getRow());
                    list.add(obj);
                } else {
                    LOGGER.debug("row is null, result is {}", rowResult.getError());
                }
            });
        }
        return list;
    }

//    class TIterable implements Iterable<T> {
//        private
//        @Override
//        public Iterator<T> iterator() {
//            return new Iterable<T>() {
//                @Override
//                public Iterator<T> iterator() {
//                    return null;
//                }
//
//                private int index = 0;
//                public boolean hasNext() {return index < words.length;}
//                public String next() { return words[index++];    }
//                public void remove() { // Not implemented
//                    throw new UnsupportedOperationException();
//                }
//            };
//        }
//    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(ID id) {
        TableStoreEntityInformation entityInfomation = (TableStoreEntityInformation) client.getEntityInfomation();
        DeleteRowResponse deleteRowResponse = deleteRow(entityInfomation, id);
        LOGGER.debug("delete {}",deleteRowResponse);
    }

    @Override
    public void delete(T entity) {
        TableStoreEntityInformation entityInfomation = (TableStoreEntityInformation) client.getEntityInfomation();
        Map<String, ColumnType> keyColumns = entityInfomation.getKeyColumns();

        PersistentPropertyAccessor propertyAccessor = entityInfomation.getEntity().getPropertyAccessor(entity);
        Key key = new Key();
        keyColumns.forEach((name, type) -> {
            Object value = propertyAccessor.getProperty(entityInfomation.getEntity().getPersistentProperty(name));
            if (value == null) {
                throw new IllegalArgumentException("keyColumns must not be null");
            }
            key.put(name, value);
        });
        deleteRow(entityInfomation, key);
    }

    private DeleteRowResponse deleteRow(TableStoreEntityInformation entityInfomation, Key key) {
        Map<String, ColumnType> keyColumns = entityInfomation.getKeyColumns();
        RowDeleteChange rowDeleteChange = new RowDeleteChange(entityInfomation.getEntityName(), buildPrimaryKey(key, keyColumns, entityInfomation.getColumnNames()));
        Future<DeleteRowResponse> deleteRowRequestFuture = client.getAsyncClient().deleteRow(new DeleteRowRequest(rowDeleteChange), null);
        DeleteRowResponse deleteRowResponse = Utils.waitForFuture(deleteRowRequestFuture);
        return deleteRowResponse;
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {

    }

    @Override
    public void deleteAll() {

    }

}
