package in.togetu.tablestore.repository.support;

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;

public class TableStorePersistentEntity<T, P extends TableStorePersistentProperty<P>> extends BasicPersistentEntity<T, P> {
    public TableStorePersistentEntity(TypeInformation<T> information) {
        super(information);
    }

}
