package uk.gov.justice.digital.noms.delius.data.api

import spock.lang.Specification

class CaseNoteTest extends Specification {

    def "Cannot build an incomplete CaseNote"() {

        setup:
        def caseNoteBuilder = CaseNote.builder()
        when:
        caseNoteBuilder.build()
        then:
        Exception e = thrown()
        e instanceof NullPointerException

    }
}
