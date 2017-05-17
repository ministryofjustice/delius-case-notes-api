package uk.gov.justice.digital.noms.delius.service;

import org.joda.time.DateTime;
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

    @Autowired
    public CaseNotesService(final JpaContactRepository contactRepository,
                            final DeliusCaseNotesTransformer transformer,
                            final ContactFactory contactFactory) {
        this.contactRepository = contactRepository;
        this.transformer = transformer;
        this.contactFactory = contactFactory;
    }

    @Override
    public Statuses createOrUpdateCaseNote(CaseNote caseNote) {
        DeliusCaseNote deliusCaseNote = transformer.toDeliusCaseNote(caseNote);

        Optional<Contact> existingContact = contactRepository.findByNomisCaseNoteID(deliusCaseNote.getNoteId());
        final Statuses status;
        if (existingContact.isPresent()) {
            status = updateContact(existingContact.get(), deliusCaseNote);
        } else {
            status = createContact(caseNote);
        }

        return status;
    }


    private Statuses createContact(CaseNote caseNote) {
        contactRepository.save(contactFactory.deliusContactFrom(caseNote));
        return Statuses.CREATED;
    }


    private Statuses updateContact(Contact contact, DeliusCaseNote deliusCaseNote) {
        DateTime existingTimestamp = new DateTime(contact.getContactDate());
        if (!deliusCaseNote.getTimestamp().isAfter(existingTimestamp)) {
            return Statuses.CONFLICT;
        }
        contact.setNotes(deliusCaseNote.getContent());
        contactRepository.save(contact);
        return Statuses.UPDATED;
    }

}
