package uk.gov.justice.digital.noms.delius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.digital.noms.delius.jpa.Contact;

import java.util.Optional;

public interface JpaContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByNomisCaseNoteID(Long nomisCaseNoteId);
}
