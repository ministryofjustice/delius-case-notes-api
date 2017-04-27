package uk.gov.justice.digital.noms.delius

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

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
    }

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
        def result = new RESTClient("http://localhost:8090/delius/casenote/")
                .put(
                path: "nomis1234/note1234",
                body: body,
                requestContentType: "application/json")

        then:
        result.status == 201
    }

    def "Unhappy path: bad requests are rejected"() {
        when:
        new RESTClient("http://localhost:8090/delius/casenote/")
                .put(
                path: "nomis1234/note1234",
                body: "{\"utter\":\"rubbish\"}",
                requestContentType: "application/json")


        then:
        Exception e = thrown()
        e instanceof HttpResponseException
        e.statusCode == HttpStatus.BAD_REQUEST.value()
    }
}
