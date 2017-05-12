package uk.gov.justice.digital.noms.delius.service;

import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.jpa.Contact;

public interface ContactFactory {
    Contact deliusContactFrom(CaseNote caseNote);
}
