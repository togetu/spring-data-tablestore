package in.togetu.tablestore.repository;

import com.alicloud.openservices.tablestore.*;
import com.alicloud.openservices.tablestore.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class Utils {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    static <Res> Res waitForFuture(Future<Res> f) {
        try {
            return f.get();
        } catch (InterruptedException e) {
            logger.error("The thread was interrupted", e);
            return null;
        } catch (ExecutionException e) {
            logger.error("The thread was aborted", e);
            return null;
        }
    }

    public static void main(String[] args) {
        AsyncClientInterface asyncClient = new AsyncClient("https://timeline-test.ap-south-1.ots.aliyuncs.com", "LTAIHzdYHvbX1Q0a", "WJNwvXuWwZaadL8A40cWnJNBMrbHsv", "timeline-test");
        Map<String, PrimaryKeyType> keyColumns = new LinkedHashMap<>();
        keyColumns.put("user", PrimaryKeyType.STRING);// 主键
        keyColumns.put("timeStamp", PrimaryKeyType.INTEGER);// 主键
        keyColumns.put("ref", PrimaryKeyType.STRING);// 主键
//        keyColumns.put("uid", PrimaryKeyType.INTEGER);// 分区键
//        keyColumns.put("refAcct", PrimaryKeyType.INTEGER);
//        keyColumns.put("mobile", PrimaryKeyType.INTEGER);
//        keyColumns.put("psw", PrimaryKeyType.INTEGER);
//        keyColumns.put("regSource", PrimaryKeyType.INTEGER);
//        keyColumns.put("regTime", PrimaryKeyType.INTEGER);
//        keyColumns.put("status", PrimaryKeyType.INTEGER);
//        keyColumns.put("nickname", PrimaryKeyType.INTEGER);
//        keyColumns.put("avatar", PrimaryKeyType.INTEGER);
//        keyColumns.put("birth", PrimaryKeyType.INTEGER);
//        keyColumns.put("sex", PrimaryKeyType.INTEGER);
//        keyColumns.put("school", PrimaryKeyType.INTEGER);
//        keyColumns.put("type", PrimaryKeyType.INTEGER);

        // 建表
        createTable(asyncClient, "likes", keyColumns);
//        Map<String, PrimaryKeyType> keyColumns2 = new LinkedHashMap<>();
//        keyColumns2.put("rid", PrimaryKeyType.STRING);// 主键
//        createTable(asyncClient, "t_account_ref", keyColumns2);
//        deleteTable(asyncClient.asSyncClient(), "account");

        // 准备主键：增删查都需要主键
//        RowPutChange rowChange = new RowPutChange("account");
//        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
//                .addPrimaryKeyColumn("refId", PrimaryKeyValue.fromString("g,xdsdwefewd"))
//                .addPrimaryKeyColumn("uid", PrimaryKeyValue.fromLong(0)).build();
//        rowChange.setPrimaryKey(primaryKey);

        // 写一行：同时写属性值
//        rowChange.addColumn("nickname", ColumnValue.fromString("name_jack"));
//        rowChange.addColumn("sex", ColumnValue.fromLong(0));
//        asyncClient.asSyncClient().putRow(new PutRowRequest(rowChange));

        // 删一行：根据主键删除
//        RowDeleteChange rowDeleteChange = new RowDeleteChange("account", primaryKey);
//        System.out.println(asyncClient.asSyncClient().deleteRow(new DeleteRowRequest(rowDeleteChange)));
//        System.out.println("del done");

        // 读一行：根据完整主键读取
//        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria("account", primaryKey);
        // 设置读取最新版本
//        criteria.setMaxVersions(1);
//        GetRowResponse getRowResponse = asyncClient.asSyncClient().getRow(new GetRowRequest(criteria));
//        Row row = getRowResponse.getRow();
//        System.out.println("读取完毕, 结果为: ");
//        System.out.println(row);

        // 展示表
//        listTableWithFuture(asyncClient);
        asyncClient.shutdown();
        System.out.println("shut down");
    }

    private static void createTable(AsyncClientInterface client, String tableName, Map<String, PrimaryKeyType> keyColumns)
            throws TableStoreException, ClientException {
        TableMeta tableMeta = new TableMeta(tableName);
        keyColumns.forEach((k, v) -> {
            tableMeta.addPrimaryKeyColumn(k, v);
        });

        TableOptions tableOptions = new TableOptions();
        tableOptions.setTimeToLive(-1);
        tableOptions.setMaxVersions(1);

        // 将该表的读写CU都设置为0
        CapacityUnit capacityUnit = new CapacityUnit(0, 0);

        CreateTableRequest request = new CreateTableRequest(tableMeta, tableOptions);
        request.setTableMeta(tableMeta);
        request.setReservedThroughput(new ReservedThroughput(capacityUnit));
        client.asSyncClient().createTable(request);

        System.out.println("表已创建");
    }

    private static void deleteTable(SyncClientInterface client, String tableName) {
        DeleteTableRequest request = new DeleteTableRequest(tableName);
        client.deleteTable(request);
    }


    private static void listTableWithFuture(AsyncClientInterface asyClient) {
        // 通过Future同步的等待结果返回。
        try {
            Future<ListTableResponse> future = asyClient.listTable(null);
            ListTableResponse result = future.get(); // 同步的等待
            System.out.println("\nList table by listTableWithFuture:");
            for (String tableName : result.getTableNames()) {
                descTable(asyClient, tableName);
            }
        } catch (TableStoreException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void descTable(AsyncClientInterface asyncClient , String tableName) {
        DescribeTableRequest request = new DescribeTableRequest(tableName);
        DescribeTableResponse response = asyncClient.asSyncClient().describeTable(request);
        TableMeta tableMeta = response.getTableMeta();
        System.out.println("表的名称：" + tableMeta.getTableName());
        System.out.println("表的主键：");
        for (PrimaryKeySchema primaryKeySchema : tableMeta.getPrimaryKeyList()) {
            System.out.println(primaryKeySchema);
        }
        TableOptions tableOptions = response.getTableOptions();
        System.out.println("表的TTL:" + tableOptions.getTimeToLive());
        System.out.println("表的MaxVersions:" + tableOptions.getMaxVersions());
        ReservedThroughputDetails reservedThroughputDetails = response.getReservedThroughputDetails();
        System.out.println("表的预留读吞吐量："
                + reservedThroughputDetails.getCapacityUnit().getReadCapacityUnit());
        System.out.println("表的预留写吞吐量："
                + reservedThroughputDetails.getCapacityUnit().getWriteCapacityUnit());
    }
}
