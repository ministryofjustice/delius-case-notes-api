package uk.gov.justice.digital.noms.delius.service

import org.joda.time.DateTime
import spock.lang.Specification
import uk.gov.justice.digital.noms.delius.data.api.CaseNote
import uk.gov.justice.digital.noms.delius.data.api.CaseNoteBody
import uk.gov.justice.digital.noms.delius.jpa.Contact
import uk.gov.justice.digital.noms.delius.repository.JpaContactRepository
import uk.gov.justice.digital.noms.delius.transformers.DeliusCaseNotesTransformer

class CaseNotesServiceTest extends Specification {


    def "service behaves appropriately"() {
        setup:
        def mockContactRepository = Mock(JpaContactRepository.class)
        def mockContactFactory = Mock(ContactFactory.class)
        def service = new CaseNotesService(mockContactRepository, new DeliusCaseNotesTransformer(), mockContactFactory)
        def timestamp = DateTime.now()
        def caseNoteBody = CaseNoteBody.builder()
                .content("content")
                .establishmentCode("establishmentCode")
                .noteType("noteType")
                .staffName("staffName")
                .timestamp(timestamp)
                .build()

        def caseNote = CaseNote.builder()
                .nomisId("1234")
                .noteId(5678l)
                .body(caseNoteBody)
                .build()

        def aContact = Optional.of(Contact.builder().contactDate(timestamp.minusHours(1).toDate()).build())
        when:
        mockContactRepository.findByNomisCaseNoteID(_) >> aContact
        mockContactRepository.save(aContact) >> aContact
        def status = service.createOrUpdateCaseNote(caseNote)

        then:
        status == Service.Statuses.UPDATED

    }

    def "Cannot update with a timestamp the same as existing"() {
        setup:
        def mockContactRepository = Mock(JpaContactRepository.class)
        def mockContactFactory = Mock(ContactFactory.class)
        def service = new CaseNotesService(mockContactRepository, new DeliusCaseNotesTransformer(), mockContactFactory)
        def timestamp = DateTime.now()
        def caseNoteBody = CaseNoteBody.builder()
                .content("content")
                .establishmentCode("establishmentCode")
                .noteType("noteType")
                .staffName("staffName")
                .timestamp(timestamp)
                .build()

        def caseNote = CaseNote.builder()
                .nomisId("1234")
                .noteId(5678l)
                .body(caseNoteBody)
                .build()

        def aContact = Optional.of(Contact.builder().contactDate(timestamp.toDate()).build())
        when:
        mockContactRepository.findByNomisCaseNoteID(_) >> aContact
        mockContactRepository.save(aContact) >> aContact
        def status = service.createOrUpdateCaseNote(caseNote)

        then:
        status == Service.Statuses.CONFLICT

    }

    def "Cannot update with a timestamp earlier than existing"() {
        setup:
        def mockContactRepository = Mock(JpaContactRepository.class)
        def mockContactFactory = Mock(ContactFactory.class)
        def service = new CaseNotesService(mockContactRepository, new DeliusCaseNotesTransformer(), mockContactFactory)
        def timestamp = DateTime.now()
        def caseNoteBody = CaseNoteBody.builder()
                .content("content")
                .establishmentCode("establishmentCode")
                .noteType("noteType")
                .staffName("staffName")
                .timestamp(timestamp.minusDays(1))
                .build()

        def caseNote = CaseNote.builder()
                .nomisId("1234")
                .noteId(5678l)
                .body(caseNoteBody)
                .build()

        def aContact = Optional.of(Contact.builder().contactDate(timestamp.toDate()).build())
        when:
        mockContactRepository.findByNomisCaseNoteID(_) >> aContact
        mockContactRepository.save(aContact) >> aContact
        def status = service.createOrUpdateCaseNote(caseNote)

        then:
        status == Service.Statuses.CONFLICT

    }
}