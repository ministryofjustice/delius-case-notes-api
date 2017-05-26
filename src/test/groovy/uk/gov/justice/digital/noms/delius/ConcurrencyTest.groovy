package uk.gov.justice.digital.noms.delius

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import groovyx.net.http.AsyncHTTPBuilder
import groovyx.net.http.Method
import org.joda.time.DateTime
import spock.lang.Ignore
import spock.lang.Specification
import uk.gov.justice.digital.noms.delius.data.api.CaseNoteBody

class ConcurrencyTest extends Specification{

    @Ignore
    def "Can handle concurrent create/update" () {
        setup:
        def http = new AsyncHTTPBuilder(
                poolSize : 5,
                uri : 'http://localhost:8090/delius/casenote/A1458AE/9018',
                contentType : "application/json" )

        def now = DateTime.now()

        def caseNoteBody = CaseNoteBody.builder()
                .noteType("OBSERVE THREAT_ST")
                .content("Mary")
                .contactTimestamp(now)
                .staffName("staffName")
                .establishmentCode("LEI")
                .build()

        def objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        when:
        def caseNote1 = objectMapper.writeValueAsString(caseNoteBody)
        def caseNote2 = objectMapper.writeValueAsString(caseNoteBody.toBuilder().contactTimestamp(now.plusMinutes(1)).content("had").build())
        def caseNote3 = objectMapper.writeValueAsString(caseNoteBody.toBuilder().contactTimestamp(now.plusMinutes(2)).content("a").build())
        def caseNote4 = objectMapper.writeValueAsString(caseNoteBody.toBuilder().contactTimestamp(now.plusMinutes(3)).content("little").build())
        def caseNote5 = objectMapper.writeValueAsString(caseNoteBody.toBuilder().contactTimestamp(now.plusMinutes(4)).content("lamb").build())

        http.request(Method.PUT) { body = caseNote3 }
        http.request(Method.PUT) { body = caseNote1 }
        http.request(Method.PUT) { body = caseNote5 }
        http.request(Method.PUT) { body = caseNote4 }
        http.request(Method.PUT) { body = caseNote2 }

        then:
        Thread.sleep(5000)


    }

}
