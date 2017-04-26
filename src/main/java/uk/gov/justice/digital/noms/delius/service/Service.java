package uk.gov.justice.digital.noms.delius.service;

import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote;

import java.util.Optional;

public interface Service {

    Optional<DeliusCaseNote> addCaseNote(CaseNote caseNote);
}
