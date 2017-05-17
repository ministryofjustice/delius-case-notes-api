package uk.gov.justice.digital.noms.delius.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.data.api.CaseNoteBody;
import uk.gov.justice.digital.noms.delius.service.CaseNotesService;
import uk.gov.justice.digital.noms.delius.service.Service;

@RestController
@RequestMapping("${base.url:/delius}")
public class CaseNotesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Service caseNotesService;

    @Autowired
    public CaseNotesController(Service caseNotesService) {
        this.caseNotesService = caseNotesService;
    }

    @RequestMapping(value = "/casenote/{nomisId}/{noteId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> putCaseNote(final @PathVariable("nomisId") String nomisId,
                                                     final @PathVariable("noteId") Long noteId,
                                                     final @RequestBody CaseNoteBody caseNoteBody) {

        logger.info("Received PUT for nomisId {}, noteId {} : {}", nomisId, noteId, caseNoteBody);

        final CaseNote caseNote = CaseNote.builder()
                .noteId(noteId)
                .nomisId(nomisId)
                .body(caseNoteBody)
                .build();


        CaseNotesService.Statuses status = caseNotesService.createOrUpdateCaseNote(caseNote);

        return new ResponseEntity<>(httpStatusOf(status));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class, IllegalArgumentException.class})
    public String badRequest(Exception e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({IllegalStateException.class})
    public String illegalState(Exception e) {
        return e.getMessage();
    }

    private HttpStatus httpStatusOf(Service.Statuses status) {
        final HttpStatus httpStatus;
        switch (status) {
            case CREATED:
                httpStatus = HttpStatus.CREATED;
                break;
            case CONFLICT:
                httpStatus = HttpStatus.CONFLICT;
                break;
            default:
                httpStatus = HttpStatus.NO_CONTENT;
        }
        return httpStatus;
    }

}
