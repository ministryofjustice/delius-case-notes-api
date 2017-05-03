package uk.gov.justice.digital.noms.delius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.digital.noms.delius.jpa.ContactType;

import java.util.Optional;

public interface JpaContactTypeRepository extends JpaRepository<ContactType, String> {
    Optional<ContactType> findByNomisContactType(String nomisContactType);
}
