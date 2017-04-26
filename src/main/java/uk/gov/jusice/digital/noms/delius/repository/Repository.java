package uk.gov.jusice.digital.noms.delius.repository;

import uk.gov.jusice.digital.noms.delius.data.delius.DeliusCaseNote;

public interface Repository {

    boolean createCaseNote(DeliusCaseNote caseNote);
}
