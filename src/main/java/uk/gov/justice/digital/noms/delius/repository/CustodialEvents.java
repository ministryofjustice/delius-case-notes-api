package uk.gov.justice.digital.noms.delius.repository;

import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.noms.delius.jpa.Event;

import java.util.List;
import java.util.Optional;

@Service
public class CustodialEvents implements CustodialEventsService {

    private final JpaEventRepository eventRepository;

    @Autowired
    public CustodialEvents(JpaEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Optional<Event> currentCustodialEvent(Long offenderId) {
        List<Event> events = eventRepository.findByOffenderOffenderIDAndDisposalTerminationDateIsNullAndDisposalDisposalTypeSentenceTypeIn(offenderId, ImmutableSet.of("NC", "SC"));
        if (events.isEmpty()) {
            return Optional.empty();
        }

        if (events.size() > 1) {
            throw new IllegalStateException("Multiple current custodial events found:" + events.toString());
        }

        return Optional.of(events.get(0));
    }
}
