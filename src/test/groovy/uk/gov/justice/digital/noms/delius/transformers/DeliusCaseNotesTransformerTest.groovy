package uk.gov.justice.digital.noms.delius.transformers

import org.joda.time.DateTime
import spock.lang.Specification
import uk.gov.justice.digital.noms.delius.data.api.CaseNote
import uk.gov.justice.digital.noms.delius.data.api.CaseNoteBody
import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote

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
                .contactTimestamp(now)
                .systemTimestamp(now)
                .build()

        def caseNote = CaseNote.builder()
                .nomisId("1234")
                .noteId(5678l)
                .body(caseNoteBody)
                .build()

        def result = transformer.toDeliusCaseNote(caseNote)

        then:
        def expected = DeliusCaseNote.builder()
                .content("content")
                .establishmentCode("establishmentCode")
                .nomisId("1234")
                .noteId(5678l)
                .noteType("noteType")
                .staffName("staffName")
                .contactTimestamp(now)
                .raisedTimestamp(now)
                .build()
        result == expected
    }
}
