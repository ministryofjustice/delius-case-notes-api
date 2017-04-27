package uk.gov.justice.digital.noms.delius.service;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote;
import uk.gov.justice.digital.noms.delius.repository.Repository;
import uk.gov.justice.digital.noms.delius.transformers.DeliusCaseNotesTransformer;

import java.util.Optional;

@org.springframework.stereotype.Service
public class CaseNotesService implements Service {

    private final Repository repository;
    private final DeliusCaseNotesTransformer transformer;

    @Autowired
    public CaseNotesService(final Repository repository, final DeliusCaseNotesTransformer transformer) {
        this.repository = repository;
        this.transformer = transformer;
    }

    @Override
    public Optional<DeliusCaseNote> addCaseNote(CaseNote caseNote) {
        Optional<DeliusCaseNote> response = Optional.empty();
        DeliusCaseNote deliusCaseNote = transformer.toDeliusCaseNote(caseNote);
        boolean success = repository.createCaseNote(deliusCaseNote);

        if (success) {
            response = Optional.of(deliusCaseNote);
        }

        return response;

    }

}
