package uk.gov.justice.digital.noms.delius;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
public class DeliusCaseNotesAPI {

    public static void main(String[] args) {
        SpringApplication.run(DeliusCaseNotesAPI.class, args);
    }

    @Autowired
    public void confiureObjectMapper(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
        jackson2ObjectMapperBuilder
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .modules(new JodaModule())
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}