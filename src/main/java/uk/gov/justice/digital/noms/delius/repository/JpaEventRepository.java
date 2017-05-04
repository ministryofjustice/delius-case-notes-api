package uk.gov.justice.digital.noms.delius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.digital.noms.delius.jpa.Event;

import java.util.List;
import java.util.Set;

public interface JpaEventRepository extends JpaRepository<Event, Long>{

    List<Event> findByOffenderOffenderIDAndDisposalTerminationDateIsNullAndDisposalDisposalTypeSentenceTypeIn(Long offenderId, Set<String> disposalTypes);

}
