package uk.gov.justice.digital.noms.delius.service;

import uk.gov.justice.digital.noms.delius.data.api.CaseNote;

public interface Service {

    enum Statuses {CREATED, UPDATED};

    CaseNotesService.Statuses createOrUpdateCaseNote(CaseNote caseNote);
}
