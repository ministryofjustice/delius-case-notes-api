package uk.gov.justice.digital.noms.delius;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;
import uk.gov.justice.digital.noms.delius.config.Configuration;
import uk.gov.justice.digital.noms.delius.config.ObjectMapperFactory;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.data.api.CaseNoteBody;
import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote;
import uk.gov.justice.digital.noms.delius.service.Service;

import java.util.Optional;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.port;
import static spark.Spark.put;

public class DeliusCaseNotesAPI {

    private static final ObjectMapper objectMapper = ObjectMapperFactory.newObjectMapper();

    public static void main(String[] args) {

        run(new Configuration());
    }

    public static void run(Configuration configuration) {

        Injector injector = Guice.createInjector(configuration);
        Service service = injector.getInstance(Service.class);

        port(injector.getInstance(Key.get(Integer.class, Names.named("port"))));

        before((request, response) -> response.type("application/json"));

        put("/casenote/:nomisId/:noteId", (Request req, Response res) ->
                {
                    CaseNoteBody caseNoteBody = objectMapper.readValue(req.body(), CaseNoteBody.class);
                    CaseNote caseNote = CaseNote.builder()
                            .nomisId(req.params(":nomisId"))
                            .noteId(req.params(":noteId"))
                            .body(caseNoteBody)
                            .build();

                    Optional<DeliusCaseNote> created = service.addCaseNote(caseNote);

                    // For now, assume any fail is a 500 until we have some repository behaviour to model
                    int status = created.isPresent() ? HttpStatus.CREATED_201 : HttpStatus.INTERNAL_SERVER_ERROR_500;
                    res.status(status);

                    return created.orElse(null);
                }
        , objectMapper::writeValueAsString);

        exception(JsonMappingException.class, (e, req,res) -> {
            res.status(HttpStatus.BAD_REQUEST_400);
            res.body(e.getMessage());
        });
    }

}
