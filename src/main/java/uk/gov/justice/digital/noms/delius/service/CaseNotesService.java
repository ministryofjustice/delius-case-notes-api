package uk.gov.justice.digital.noms.delius.service;

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
            updateContact(existingContact.get(), deliusCaseNote);
            status = Statuses.UPDATED;
        } else {
            createContact(caseNote);
            status = Statuses.CREATED;
        }

        return status;
    }



    private Contact createContact(CaseNote caseNote) {
        return contactRepository.save(contactFactory.deliusContactFrom(caseNote));
    }



    private Contact updateContact(Contact contact, DeliusCaseNote deliusCaseNote) {
        contact.setNotes(deliusCaseNote.getContent());
        return contactRepository.save(contact);
    }

}
