package uk.gov.justice.digital.noms.delius.repository;

import uk.gov.justice.digital.noms.delius.jpa.Event;

import java.util.Optional;

public interface CustodialEventsService {

    Optional<Event> currentCustodialEvent(Long offenderId);
}
