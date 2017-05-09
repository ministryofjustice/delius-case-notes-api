package uk.gov.justice.digital.noms.delius.service;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.data.delius.DeliusCaseNote;
import uk.gov.justice.digital.noms.delius.jpa.Contact;
import uk.gov.justice.digital.noms.delius.jpa.ContactType;
import uk.gov.justice.digital.noms.delius.jpa.Event;
import uk.gov.justice.digital.noms.delius.jpa.Offender;
import uk.gov.justice.digital.noms.delius.jpa.ProbationArea;
import uk.gov.justice.digital.noms.delius.jpa.Staff;
import uk.gov.justice.digital.noms.delius.jpa.Team;
import uk.gov.justice.digital.noms.delius.repository.CustodialEventsService;
import uk.gov.justice.digital.noms.delius.repository.JpaContactRepository;
import uk.gov.justice.digital.noms.delius.repository.JpaContactTypeRepository;
import uk.gov.justice.digital.noms.delius.repository.JpaOffenderRepository;
import uk.gov.justice.digital.noms.delius.repository.JpaProbationAreaRepository;
import uk.gov.justice.digital.noms.delius.repository.StaffService;
import uk.gov.justice.digital.noms.delius.transformers.DeliusCaseNotesTransformer;

import java.util.Date;
import java.util.Optional;

@org.springframework.stereotype.Service
public class CaseNotesService implements Service {

    private final JpaContactRepository contactRepository;
    private final JpaContactTypeRepository contactTypeRepository;
    private final JpaOffenderRepository offenderRepository;
    private final StaffService staffService;
    private final JpaProbationAreaRepository probationAreaRepository;

    private final CustodialEventsService custodialEventsService;
    private final DeliusCaseNotesTransformer transformer;

    @Autowired
    public CaseNotesService(final JpaContactRepository contactRepository,
                            final JpaContactTypeRepository contactTypeRepository,
                            final JpaOffenderRepository offenderRepository,
                            final JpaProbationAreaRepository probationAreaRepository,
                            final StaffService staffService,
                            final CustodialEventsService custodialEventsService,
                            final DeliusCaseNotesTransformer transformer) {
        this.contactRepository = contactRepository;
        this.contactTypeRepository = contactTypeRepository;
        this.offenderRepository = offenderRepository;
        this.staffService = staffService;
        this.probationAreaRepository = probationAreaRepository;
        this.custodialEventsService = custodialEventsService;
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

        Optional<Offender> maybeOffender = offenderRepository.findByNomsNumber(caseNote.getNomisId().toString());
        if (!maybeOffender.isPresent()) {
            throw new IllegalArgumentException("No Delius offender found for nomis id '" + caseNote.getNomisId() + "'" );
        }

        Optional<Event> maybeCurrentCustodialEvent = custodialEventsService.currentCustodialEvent(maybeOffender.get().getOffenderID());
        if (!maybeCurrentCustodialEvent.isPresent()) {
            throw new IllegalArgumentException("No current custodial event for offender '" + maybeOffender.get().toString() + "'" );
        }

        Optional<ProbationArea> maybeProbationArea = probationAreaRepository.findByProbationAreaCode(caseNote.getBody().getEstablishmentCode());

        if (!maybeProbationArea.isPresent()) {
            throw new IllegalArgumentException("No probation area found for nomis establishmentCode '" + caseNote.getBody().getEstablishmentCode() + "'" );
        }

        Optional<Staff> maybeStaff = staffService.getAppropriateStaffMemberForProbationArea(maybeProbationArea.get().getProbationAreaId());

        if (!maybeStaff.isPresent()) {
            throw new IllegalArgumentException("Could not resolve appropriate delius Staff for delius probationAreaCode '" + maybeProbationArea.get().getProbationAreaCode() + "'" );
        }

        Team team = maybeStaff.get().getTeams().iterator().next();

        Date contactDate = caseNote.getBody().getTimestamp().toDate();
        Contact contact = Contact.builder()
                .contactDate(contactDate)
                .contactStartTime(contactDate)
                .contactType(maybeContactType.get())
                .nomisCaseNoteID(caseNote.getNoteId())
                .eventId(maybeCurrentCustodialEvent.get().getEventID())
                .notes(caseNote.getBody().getContent())
                .offenderId(maybeOffender.get().getOffenderID())
                .probationAreaID(maybeProbationArea.get().getProbationAreaId())
                .staffEmployeeID(maybeStaff.get().getStaffId())
                .staffId(maybeStaff.get().getStaffId())
                .teamId(team.getTeamId())
                .build();

        return Optional.ofNullable(contactRepository.save(contact));
    }



    private Contact updateContact(Contact contact, DeliusCaseNote deliusCaseNote) {
        contact.setNotes(deliusCaseNote.getContent());
        return contactRepository.save(contact);
    }

}
