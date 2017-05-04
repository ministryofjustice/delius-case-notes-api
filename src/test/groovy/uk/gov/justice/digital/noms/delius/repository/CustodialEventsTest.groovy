package uk.gov.justice.digital.noms.delius.repository

import org.joda.time.DateTime
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import uk.gov.justice.digital.noms.delius.DeliusCaseNotesAPI
import uk.gov.justice.digital.noms.delius.jpa.Disposal
import uk.gov.justice.digital.noms.delius.jpa.DisposalType
import uk.gov.justice.digital.noms.delius.jpa.Event
import uk.gov.justice.digital.noms.delius.jpa.Offender

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class CustodialEventsTest extends Specification {


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


    }

    def "Happy path: can resolve single current custodial event where sentence type is 'NC"() {
        setup:
        def custodialEvents = context.getBean(CustodialEvents.class)
        def eventRepository = context.getBean(JpaEventRepository.class)
        def offenderRepository = context.getBean(JpaOffenderRepository.class)

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
                .sentenceType("NC")
                .build()

        def disposal = Disposal.builder()
                .event(event)
                .disposalId(1)
                .disposalType(disposalType)
                .build()

        event.setDisposal(disposal)

        eventRepository.save(event)

        when:
        def currentCustodialEvent = custodialEvents.currentCustodialEvent(666)

        then:
        currentCustodialEvent.get().getEventID() == 1
    }

    def "Happy path: can resolve single current custodial event where sentence type is 'SC'"() {
        setup:
        def custodialEvents = context.getBean(CustodialEvents.class)
        def eventRepository = context.getBean(JpaEventRepository.class)
        def offenderRepository = context.getBean(JpaOffenderRepository.class)

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

        when:
        def currentCustodialEvent = custodialEvents.currentCustodialEvent(666)

        then:
        currentCustodialEvent.get().getEventID() == 1
    }

    def "Unhappy path: cannot resolve single current custodial event where sentence type is neither 'SC' nor 'NC'"() {
        setup:
        def custodialEvents = context.getBean(CustodialEvents.class)
        def eventRepository = context.getBean(JpaEventRepository.class)
        def offenderRepository = context.getBean(JpaOffenderRepository.class)

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
                .sentenceType("Cauliflower")
                .build()

        def disposal = Disposal.builder()
                .event(event)
                .disposalId(1)
                .disposalType(disposalType)
                .build()

        event.setDisposal(disposal)

        eventRepository.save(event)

        when:
        def currentCustodialEvent = custodialEvents.currentCustodialEvent(666)

        then:
        currentCustodialEvent == Optional.empty()
    }

    def "Unhappy path: cannot resolve single current custodial event where disposal terminationDate is not null"() {
        setup:
        def custodialEvents = context.getBean(CustodialEvents.class)
        def eventRepository = context.getBean(JpaEventRepository.class)
        def offenderRepository = context.getBean(JpaOffenderRepository.class)

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
                .terminationDate(DateTime.now().toDate())
                .build()

        event.setDisposal(disposal)

        eventRepository.save(event)

        when:
        def currentCustodialEvent = custodialEvents.currentCustodialEvent(666)

        then:
        currentCustodialEvent == Optional.empty()
    }

    def "Unhappy path: cannot resolve single current custodial event where there are concurrent active disposals"() {
        setup:
        def custodialEvents = context.getBean(CustodialEvents.class)
        def eventRepository = context.getBean(JpaEventRepository.class)
        def offenderRepository = context.getBean(JpaOffenderRepository.class)

        def offender = Offender.builder()
                .nomsNumber("1234")
                .offenderID(666l)
                .softDeleted(false)
                .build()

        offenderRepository.save(offender)

        def event1 = Event.builder()
                .eventID(1)
                .offender(offender)
                .build()

        def event2 = Event.builder()
                .eventID(2)
                .offender(offender)
                .build()

        def disposalType = DisposalType.builder()
                .disposalTypeId(1)
                .sentenceType("SC")
                .build()

        def disposal1 = Disposal.builder()
                .event(event1)
                .disposalId(1)
                .disposalType(disposalType)
                .build()

        def disposal2 = Disposal.builder()
                .event(event2)
                .disposalId(2)
                .disposalType(disposalType)
                .build()

        event1.setDisposal(disposal1)
        eventRepository.save(event1)
        event2.setDisposal(disposal2)
        eventRepository.save(event2)

        when:
        custodialEvents.currentCustodialEvent(666)

        then:
        IllegalStateException ex = thrown()
        ex.message.startsWith("Multiple current custodial events found")
    }


}