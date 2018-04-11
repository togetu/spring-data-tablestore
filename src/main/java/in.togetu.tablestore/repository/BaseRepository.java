package in.togetu.tablestore.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

    <S extends T> S update(S entity, List<String> attrs);
}
