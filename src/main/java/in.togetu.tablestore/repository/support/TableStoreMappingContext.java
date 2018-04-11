package in.togetu.tablestore.repository.support;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

public class TableStoreMappingContext<E extends TableStorePersistentEntity<?, P>,
        P extends TableStorePersistentProperty<P>> extends AbstractMappingContext<E, P> implements DisposableBean {

    private Set<TableStoreClient> clients = new HashSet<>();

    @Override
    protected <T> E createPersistentEntity(TypeInformation<T> typeInformation) {
        return (E) new TableStorePersistentEntity<T, P>(typeInformation);
    }

    @Override
    protected P createPersistentProperty(Property property, E owner, SimpleTypeHolder simpleTypeHolder) {
        return (P) new TableStorePersistentProperty(property, owner, simpleTypeHolder);
    }

    @Override
    public void destroy() throws Exception {
        clients.forEach(TableStoreClient::destroy);
    }

    public void setClient(TableStoreClient client) {
        clients.add(client);
    }
}
