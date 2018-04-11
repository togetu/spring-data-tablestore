package in.togetu.tablestore.repository.support;

import com.alicloud.openservices.tablestore.AsyncClientInterface;
import com.alicloud.openservices.tablestore.TableStoreWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.repository.core.EntityInformation;

public class TableStoreClient implements DisposableBean {
    private static Logger LOGGER = LoggerFactory.getLogger(TableStoreClient.class);

    private EntityInformation entityInfomation;
    protected AsyncClientInterface asyncClient;
    protected TableStoreWriter tableStoreWriter;

    public TableStoreClient(AsyncClientInterface asyncClient, TableStoreWriter tableStoreWriter) {

    }

    public <T, ID> TableStoreClient(AsyncClientInterface asyncClient, Object o, EntityInformation<T, ID> entityInformation) {
        this.asyncClient = asyncClient;
        this.tableStoreWriter = tableStoreWriter;
        this.entityInfomation = entityInformation;

    }

    public AsyncClientInterface getAsyncClient() {
        return asyncClient;
    }

    public void setAsyncClient(AsyncClientInterface asyncClient) {
        this.asyncClient = asyncClient;
    }

    public TableStoreWriter getTableStoreWriter() {
        return tableStoreWriter;
    }

    public void setTableStoreWriter(TableStoreWriter tableStoreWriter) {
        this.tableStoreWriter = tableStoreWriter;
    }

    public EntityInformation getEntityInfomation() {
        return entityInfomation;
    }

    public void setEntityInfomation(EntityInformation entityInfomation) {
        this.entityInfomation = entityInfomation;
    }

    @Override
    public void destroy() {
        if (this.tableStoreWriter != null) {
            this.tableStoreWriter.close();
        }

        this.asyncClient.shutdown();

        LOGGER.info("tablestore client shutdown finish.");
    }
}
