package uk.gov.justice.digital.noms.delius.transformers;

import org.springframework.stereotype.Component;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.data.api.CaseNoteBody;
import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote;

@Component
public class DeliusCaseNotesTransformer {

    public DeliusCaseNote toDeliusCaseNote(CaseNote caseNote) {

        CaseNoteBody body = caseNote.getBody();
        return DeliusCaseNote.builder()
                .content(body.getContent())
                .establishmentCode(body.getEstablishmentCode())
                .nomisId(caseNote.getNomisId())
                .noteId(caseNote.getNoteId())
                .staffName(body.getStaffName())
                .noteType(body.getNoteType())
                .contactTimestamp(body.getContactTimestamp())
                .raisedTimestamp(body.getRaisedTimestamp())
                .build();
    }
}
