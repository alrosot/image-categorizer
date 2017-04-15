package trofo.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SelectionRepository extends JpaRepository<Selection, Long> {
    Optional<Selection> findByFileAndCategory(String file, int category);
}
