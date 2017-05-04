package uk.gov.justice.digital.noms.delius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.digital.noms.delius.jpa.Offender;

import java.util.Optional;

public interface JpaOffenderRepository extends JpaRepository<Offender, Long>{
    Optional<Offender> findByNomsNumber(String nomsNumber);
}
