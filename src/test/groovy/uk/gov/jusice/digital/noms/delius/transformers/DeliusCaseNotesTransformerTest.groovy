package uk.gov.jusice.digital.noms.delius.transformers

import org.joda.time.DateTime
import spock.lang.Specification
import uk.gov.jusice.digital.noms.delius.data.api.CaseNote
import uk.gov.jusice.digital.noms.delius.data.api.CaseNoteBody
import uk.gov.jusice.digital.noms.delius.data.delius.DeliusCaseNote

class DeliusCaseNotesTransformerTest extends Specification {

    def "transformer can transform API CaseNote into Delius Case Note"() {
        setup:
        DeliusCaseNotesTransformer transformer = new DeliusCaseNotesTransformer()

        when:
        def now = DateTime.now()
        def caseNoteBody = CaseNoteBody.builder()
                .content("content")
                .establishmentCode("establishmentCode")
                .noteType("noteType")
                .staffName("staffName")
                .timestamp(now)
                .build()

        def caseNote = CaseNote.builder()
                .nomisId("nomisIs")
                .noteId("noteId")
                .body(caseNoteBody)
                .build()

        def result = transformer.toDeliusCaseNote(caseNote)

        then:
        def expected = DeliusCaseNote.builder()
                .content("content")
                .establishmentCode("establishmentCode")
                .nomisId("nomisIs")
                .noteId("noteId")
                .noteType("noteType")
                .staffName("staffName")
                .timestamp(now)
                .build()
        result == expected
    }
}
