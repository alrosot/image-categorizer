package trofo.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Long> {
    Optional<Selection> findByFileAndCategory(String file, int category);

    Collection<Selection> findByFile(String file);

    @Query("SELECT distinct category from Selection")
    Set<Integer> findCategory();

    List<Selection> findByCategory(Integer categortId);
}
