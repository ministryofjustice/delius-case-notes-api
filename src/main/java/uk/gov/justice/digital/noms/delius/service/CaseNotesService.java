package uk.gov.justice.digital.noms.delius.service;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote;
import uk.gov.justice.digital.noms.delius.jpa.Contact;
import uk.gov.justice.digital.noms.delius.jpa.ContactType;
import uk.gov.justice.digital.noms.delius.repository.JpaContactRepository;
import uk.gov.justice.digital.noms.delius.repository.JpaContactTypeRepository;
import uk.gov.justice.digital.noms.delius.transformers.DeliusCaseNotesTransformer;

import java.util.Date;
import java.util.Optional;

@org.springframework.stereotype.Service
public class CaseNotesService implements Service {

    private final JpaContactRepository contactRepository;
    private final JpaContactTypeRepository contactTypeRepository;
    private final DeliusCaseNotesTransformer transformer;

    @Autowired
    public CaseNotesService(final JpaContactRepository contactRepository,
                            final JpaContactTypeRepository contactTypeRepository,
                            final DeliusCaseNotesTransformer transformer) {
        this.contactRepository = contactRepository;
        this.contactTypeRepository = contactTypeRepository;
        this.transformer = transformer;
    }

    @Override
    public Optional<DeliusCaseNote> createOrUpdateCaseNote(CaseNote caseNote) {
        DeliusCaseNote deliusCaseNote = transformer.toDeliusCaseNote(caseNote);
        Optional<DeliusCaseNote> response = Optional.of(deliusCaseNote);

        Optional<Contact> existingContact = contactRepository.findByNomisCaseNoteID(deliusCaseNote.getNoteId());

        if (existingContact.isPresent()) {
            updateContact(existingContact.get(), deliusCaseNote);
        } else {
            Optional<Contact> contact = createContact(caseNote);
            if (!contact.isPresent()) {
                response = Optional.empty();
            }
        }

        return response;

    }

    private Optional<Contact> createContact(CaseNote caseNote) {
        String noteType = caseNote.getBody().getNoteType();
        Optional<ContactType> maybeContactType = contactTypeRepository.findByNomisContactType(noteType);

        if (!maybeContactType.isPresent()) {
            throw new IllegalArgumentException("No Delius contact type found for nomis contact type '" + noteType + "'" );
        }

        Date contactDate = caseNote.getBody().getTimestamp().toDate();
        Contact contact = Contact.builder()
                .contactDate(contactDate)
                .contactStartTime(contactDate)
                .contactType(maybeContactType.get())
                .nomisCaseNoteID(caseNote.getNoteId())
                // TODO: Lookup "current custodial event"
                // .eventId(???)
                .notes(caseNote.getBody().getContent())
                // TODO: Lookup offender from nomis id
                // .offenderId(???)
                // TODO: lookup 'staff member' and from it provide:
                // .probationAreaID(???)
                // .staffEmployeeID(???)
                // .staffId(???)
                // .teamId(???)
                .build();

        return Optional.ofNullable(contactRepository.save(contact));
    }

    private Contact updateContact(Contact contact, DeliusCaseNote deliusCaseNote) {
        contact.setNotes(deliusCaseNote.getContent());
        return contactRepository.save(contact);
    }

}
