package uk.gov.justice.digital.noms.delius.service;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote;
import uk.gov.justice.digital.noms.delius.jpa.Contact;
import uk.gov.justice.digital.noms.delius.repository.JpaContactRepository;
import uk.gov.justice.digital.noms.delius.transformers.DeliusCaseNotesTransformer;

import java.util.Optional;

@org.springframework.stereotype.Service
public class CaseNotesService implements Service {

    private final JpaContactRepository contactRepository;
    private final DeliusCaseNotesTransformer transformer;
    private final ContactFactory contactFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CaseNotesService(final JpaContactRepository contactRepository,
                            final DeliusCaseNotesTransformer transformer,
                            final ContactFactory contactFactory) {
        this.contactRepository = contactRepository;
        this.transformer = transformer;
        this.contactFactory = contactFactory;
    }

    @Override
//    @Transactional
//    @Transactional(isolation = Isolation.SERIALIZABLE) TODO: This should work, but not with H2
    public synchronized Statuses createOrUpdateCaseNote(CaseNote caseNote) {
        DeliusCaseNote deliusCaseNote = transformer.toDeliusCaseNote(caseNote);

        Optional<Contact> existingContact = contactRepository.findByNomisCaseNoteID(deliusCaseNote.getNoteId());
       final Statuses status = existingContact.map(contact -> updateContact(contact, deliusCaseNote)).orElseGet(() -> createContact(caseNote));

        return status;
    }

    private Statuses createContact(CaseNote caseNote) {
        Contact contact = contactFactory.deliusContactFrom(caseNote);
        logger.info("creating {}", contact);
        contactRepository.save(contact);
        return Statuses.CREATED;
    }


    private Statuses updateContact(Contact contact, DeliusCaseNote deliusCaseNote) {
        logger.info("Updating with {}", deliusCaseNote);
        DateTime existingTimestamp = new DateTime(contact.getLastUpdatedDateTime());
        logger.info("comparing new timestamp {} to existing {}", deliusCaseNote.getRaisedTimestamp(), existingTimestamp);
        if (!deliusCaseNote.getRaisedTimestamp().isAfter(existingTimestamp)) {
            return Statuses.CONFLICT;
        }
        contact.setNotes(deliusCaseNote.getContent());
        contact.setLastUpdatedDateTime(deliusCaseNote.getRaisedTimestamp().toDate());
        contactRepository.save(contact);
        return Statuses.UPDATED;
    }

}
