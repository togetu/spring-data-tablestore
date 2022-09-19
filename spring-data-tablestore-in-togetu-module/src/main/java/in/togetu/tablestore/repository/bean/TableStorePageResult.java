package in.togetu.tablestore.repository.bean;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class TableStorePageResult<T> extends PageImpl<T> {
    private Key nextKey;

    public TableStorePageResult(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public TableStorePageResult(List<T> content) {
        super(content);
    }

    public Key getNextKey() {
        return nextKey;
    }

    public void setNextKey(Key nextKey) {
        this.nextKey = nextKey;
    }
}
