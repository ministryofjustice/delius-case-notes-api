package uk.gov.justice.digital.noms.delius

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import uk.gov.justice.digital.noms.delius.jpa.Contact
import uk.gov.justice.digital.noms.delius.jpa.ContactType
import uk.gov.justice.digital.noms.delius.jpa.Disposal
import uk.gov.justice.digital.noms.delius.jpa.DisposalType
import uk.gov.justice.digital.noms.delius.jpa.Event
import uk.gov.justice.digital.noms.delius.jpa.Offender
import uk.gov.justice.digital.noms.delius.repository.JpaContactRepository
import uk.gov.justice.digital.noms.delius.repository.JpaContactTypeRepository
import uk.gov.justice.digital.noms.delius.repository.JpaEventRepository
import uk.gov.justice.digital.noms.delius.repository.JpaOffenderRepository

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class DeliusCaseNotesAPITest extends Specification {

    @Shared
    @AutoCleanup
    ConfigurableApplicationContext context

    void setupSpec() {
        Future future = Executors
                .newSingleThreadExecutor().submit(
                new Callable() {
                    @Override
                    public ConfigurableApplicationContext call() throws Exception {
                        return (ConfigurableApplicationContext) SpringApplication
                                .run(DeliusCaseNotesAPI.class, "--server.port=8090")
                    }
                })
        context = future.get(60, TimeUnit.SECONDS)

        def contactTypeRepository = context.getBean(JpaContactTypeRepository.class)
        def contactType = new ContactType()
        contactType.setContactTypeID(1234l)
        contactType.setNomisContactType("nomisNoteType")
        contactTypeRepository.save(contactType)

        def offenderRepository = context.getBean(JpaOffenderRepository.class)
        def eventRepository = context.getBean(JpaEventRepository.class)
        def offender = Offender.builder()
                .nomsNumber("1234")
                .offenderID(666l)
                .softDeleted(false)
                .build()

        offenderRepository.save(offender)

        def event = Event.builder()
                .eventID(1)
                .offender(offender)
                .build()

        def disposalType = DisposalType.builder()
                .disposalTypeId(1)
                .sentenceType("SC")
                .build()

        def disposal = Disposal.builder()
                .event(event)
                .disposalId(1)
                .disposalType(disposalType)
                .build()

        event.setDisposal(disposal)

        eventRepository.save(event)



    }

    def "Happy path: Can put new case note"() {

        setup:
        def body = "{\n" +
                "  \"noteType\": \"nomisNoteType\",\n" +
                "  \"content\": \"content\",\n" +
                "  \"timestamp\": \"2017-04-26T09:35:00.833Z\",\n" +
                "  \"staffName\": \"staffName\",\n" +
                "  \"establishmentCode\": \"establishmentCode\"\n" +
                "}"

        when:
        def contactRepository = context.getBean(JpaContactRepository.class)

        def result = new RESTClient("http://localhost:8090/delius/casenote/")
                .put(
                path: "1234/5678",
                body: body,
                requestContentType: "application/json")

        then:
        result.status == 201
        contactRepository.findByNomisCaseNoteID(5678l).isPresent()
    }

    def "Happy path: Can update existing case note"() {

        setup:
        def contactRepository = context.getBean(JpaContactRepository.class)
        def contact = Contact.builder()
            .nomisCaseNoteID(6666l)
            .notes("old content")
            .build();

        contactRepository.save(contact)

        def body = "{\n" +
                "  \"noteType\": \"nomisNoteType\",\n" +
                "  \"content\": \"new content\",\n" +
                "  \"timestamp\": \"2017-04-26T09:35:00.833Z\",\n" +
                "  \"staffName\": \"staffName\",\n" +
                "  \"establishmentCode\": \"establishmentCode\"\n" +
                "}"

        when:
        def result = new RESTClient("http://localhost:8090/delius/casenote/")
                .put(
                path: "1234/6666",
                body: body,
                requestContentType: "application/json")

        then:
        result.status == 201
        def maybeContact = contactRepository.findByNomisCaseNoteID(6666l)
        maybeContact.isPresent()
        maybeContact.get().notes == "new content"

    }

    def "Unhappy path: bad requests are rejected"() {
        when:
        new RESTClient("http://localhost:8090/delius/casenote/")
                .put(
                path: "1234/5678",
                body: "{\"utter\":\"rubbish\"}",
                requestContentType: "application/json")


        then:
        Exception e = thrown()
        e instanceof HttpResponseException
        e.statusCode == HttpStatus.BAD_REQUEST.value()
    }
}
