package uk.gov.justice.digital.noms.delius

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification
import uk.gov.justice.digital.noms.delius.config.Configuration

class DeliusCaseNotesAPITest extends Specification {

    @Shared
    def x =  DeliusCaseNotesAPI.run(new Configuration())

    def "Happy path: Can post and write a case note"() {

        setup:
        def body = "{\n" +
                "  \"noteType\": \"noteType\",\n" +
                "  \"content\": \"content\",\n" +
                "  \"timestamp\": \"2017-04-26T09:35:00.833Z\",\n" +
                "  \"staffName\": \"staffName\",\n" +
                "  \"establishmentCode\": \"establishmentCode\"\n" +
                "}"

        when:
        def result = new RESTClient("http://localhost:8090/casenote/")
                .put(
                    path: "nomis1234/note1234",
                    body: body,
                    requestContentType: "application/json")

        then:
        result.status == 201
    }

    def "Unhappy path: bad requests are rejected"() {
        when:
        new RESTClient("http://localhost:8090/casenote/")
                .put(
                path: "nomis1234/note1234",
                body: "{\"utter\":\"rubbish\"}",
                requestContentType: "application/json")


        then:
        Exception e = thrown()
        e instanceof HttpResponseException
        e.message == "Bad Request"
    }
}
