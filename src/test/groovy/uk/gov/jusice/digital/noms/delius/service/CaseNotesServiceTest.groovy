package uk.gov.jusice.digital.noms.delius.service

import org.joda.time.DateTime
import spock.lang.Specification
import uk.gov.jusice.digital.noms.delius.data.api.CaseNote
import uk.gov.jusice.digital.noms.delius.data.api.CaseNoteBody
import uk.gov.jusice.digital.noms.delius.repository.Repository
import uk.gov.jusice.digital.noms.delius.transformers.DeliusCaseNotesTransformer

class CaseNotesServiceTest extends Specification {


    def "service behaves appropriately"() {
        setup:
        def mockRepository = Mock(Repository)
        def service = new CaseNotesService(mockRepository, new DeliusCaseNotesTransformer())
        def caseNoteBody = CaseNoteBody.builder()
                .content("content")
                .establishmentCode("establishmentCode")
                .noteType("noteType")
                .staffName("staffName")
                .timestamp(DateTime.now())
                .build()

        def caseNote = CaseNote.builder()
                .nomisId("nomisIs")
                .noteId("noteId")
                .body(caseNoteBody)
                .build()
        when:
        mockRepository.createCaseNote(_) >> true
        def deliusCaseNote = service.addCaseNote(caseNote)

        then:
        deliusCaseNote.isPresent() == true

    }
}
