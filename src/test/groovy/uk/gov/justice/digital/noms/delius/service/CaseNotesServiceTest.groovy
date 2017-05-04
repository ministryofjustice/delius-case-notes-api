package uk.gov.justice.digital.noms.delius.service

import org.joda.time.DateTime
import spock.lang.Specification
import uk.gov.justice.digital.noms.delius.data.api.CaseNote
import uk.gov.justice.digital.noms.delius.data.api.CaseNoteBody
import uk.gov.justice.digital.noms.delius.jpa.Contact
import uk.gov.justice.digital.noms.delius.repository.JpaContactRepository
import uk.gov.justice.digital.noms.delius.repository.JpaContactTypeRepository
import uk.gov.justice.digital.noms.delius.transformers.DeliusCaseNotesTransformer

class CaseNotesServiceTest extends Specification {


    def "service behaves appropriately"() {
        setup:
        def mockContactRepository = Mock(JpaContactRepository.class)
        def mockContactTypeRepository = Mock(JpaContactTypeRepository.class)
        def service = new CaseNotesService(mockContactRepository, mockContactTypeRepository, offenderRepository, eventRepository, new DeliusCaseNotesTransformer())
        def caseNoteBody = CaseNoteBody.builder()
                .content("content")
                .establishmentCode("establishmentCode")
                .noteType("noteType")
                .staffName("staffName")
                .timestamp(DateTime.now())
                .build()

        def caseNote = CaseNote.builder()
                .nomisId(1234l)
                .noteId(5678l)
                .body(caseNoteBody)
                .build()

        def aContact = Optional.of(Contact.builder().build())
        when:
        mockContactRepository.findByNomisCaseNoteID(_) >> aContact
        mockContactRepository.save(aContact) >> aContact
        def deliusCaseNote = service.createOrUpdateCaseNote(caseNote)

        then:
        deliusCaseNote.isPresent()

    }
}
