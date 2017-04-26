package uk.gov.justice.digital.noms.delius.repository;

import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote;

public interface Repository {

    boolean createCaseNote(DeliusCaseNote caseNote);
}
