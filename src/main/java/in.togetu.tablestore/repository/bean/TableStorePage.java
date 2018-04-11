package in.togetu.tablestore.repository.bean;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TableStorePage extends AbstractPageRequest {

    private final Sort sort;


    private KeyRange keyRange;

    public TableStorePage(int page, int size) {
        this(page, size, Sort.unsorted());
    }

    /**
     * Creates a new {@link AbstractPageRequest}. Pages are zero indexed, thus providing 0 for {@code page} will return
     * the first page.
     *  @param page must not be less than zero.
     * @param size must not be less than one.
     * @param sort
     */
    public TableStorePage(int page, int size, Sort sort) {
        super(page, size);
        this.sort = sort;
    }



    @Override
    public Sort getSort() {
        return this.sort;
    }


    public Pageable next() {
        return new TableStorePage(getPageNumber() + 1, getPageSize(), getSort());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.AbstractPageRequest#previous()
     */
    public TableStorePage previous() {
        return getPageNumber() == 0 ? this : new TableStorePage(getPageNumber() - 1, getPageSize(), getSort());
    }

    @Override
    public Pageable first() {
        return new TableStorePage(0, getPageSize(), getSort());
    }

    public KeyRange getKeyRange() {
        return keyRange;
    }

    public void setKeyRange(KeyRange keyRange) {
        this.keyRange = keyRange;
    }
}
