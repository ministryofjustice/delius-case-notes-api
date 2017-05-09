package uk.gov.justice.digital.noms.delius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.digital.noms.delius.jpa.ProbationArea;

import java.util.Optional;

public interface JpaProbationAreaRepository extends JpaRepository<ProbationArea, Long> {

    Optional<ProbationArea> findByProbationAreaCode(String probationAreaCode);
}
