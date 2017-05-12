package uk.gov.justice.digital.noms.delius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.jpa.Contact;
import uk.gov.justice.digital.noms.delius.repository.CustodialEventsService;
import uk.gov.justice.digital.noms.delius.repository.JpaContactTypeRepository;
import uk.gov.justice.digital.noms.delius.repository.JpaOffenderRepository;
import uk.gov.justice.digital.noms.delius.repository.JpaProbationAreaRepository;
import uk.gov.justice.digital.noms.delius.repository.StaffService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class RepositoryBackedContactFactory implements ContactFactory {

    private final JpaContactTypeRepository contactTypeRepository;
    private final JpaOffenderRepository offenderRepository;
    private final StaffService staffService;
    private final JpaProbationAreaRepository probationAreaRepository;
    private final CustodialEventsService custodialEventsService;

    @Autowired
    public RepositoryBackedContactFactory(JpaContactTypeRepository contactTypeRepository,
                                          JpaOffenderRepository offenderRepository,
                                          StaffService staffService,
                                          JpaProbationAreaRepository probationAreaRepository,
                                          CustodialEventsService custodialEventsService) {
        this.contactTypeRepository = contactTypeRepository;
        this.offenderRepository = offenderRepository;
        this.staffService = staffService;
        this.probationAreaRepository = probationAreaRepository;
        this.custodialEventsService = custodialEventsService;
    }

    private Function<Contact,Contact> contactBuilderForCaseNote(CaseNote caseNote) {

        Function<Contact, Contact> resolveContactType = cn -> Contact.builder()
                .contactType(contactTypeRepository.findByNomisContactType(caseNote.getBody().getNoteType())
                        .orElseThrow(() -> new IllegalArgumentException("No Delius contact type found for nomis contact type '" + caseNote.getBody().getNoteType() + "'" )))
                .build();


        Function<Contact, Contact> resolveOffender = contact -> contact.toBuilder()
                .offenderId(offenderRepository.findByNomsNumber(caseNote.getNomisId().toString())
                        .orElseThrow(() -> new IllegalArgumentException("No Delius offender found for nomis id '" + caseNote.getNomisId() + "'" ))
                        .getOffenderID())
                .build();


        Function<Contact,Contact> resolveCustodialEvent = contact -> contact.toBuilder()
                .eventId(custodialEventsService.currentCustodialEvent(contact.getOffenderId())
                        .orElseThrow(() -> new IllegalArgumentException("No current custodial event for offender id '" + contact.getOffenderId() + "'" ))
                        .getEventID())
                .build();

        Function<Contact,Contact> resolveProbationArea = contact -> contact.toBuilder()
                .probationAreaID(probationAreaRepository.findByProbationAreaCode(caseNote.getBody().getEstablishmentCode())
                        .orElseThrow(() -> new IllegalArgumentException("No probation area found for nomis establishmentCode '" + caseNote.getBody().getEstablishmentCode() + "'" ))
                        .getProbationAreaId())
                .build();


        Function<Contact,Contact> resolveStaff = contact -> contact.toBuilder()
                .staffId(staffService.getAppropriateStaffMemberForProbationArea(contact.getProbationAreaID())
                        .orElseThrow(() -> new IllegalArgumentException("Could not resolve appropriate delius Staff for delius probationAreaId '" + contact.getProbationAreaID() + "'" ))
                        .getStaffId())
                .build();

        Function<Contact,Contact> resolveStaffEmployee = contact -> contact.toBuilder().staffEmployeeID(contact.getStaffId()).build();

        Function<Contact,Contact> resolveTeam = contact -> contact.toBuilder()
                .teamId(staffService.getAppropriateStaffMemberForProbationArea(contact.getProbationAreaID())
                        .orElseThrow(() -> new IllegalArgumentException("Could not resolve appropriate delius Team for delius probationAreaId '" + contact.getProbationAreaID() + "'" ))
                        .getTeams().iterator().next().getTeamId())
                .build();

        Date contactDate = caseNote.getBody().getTimestamp().toDate();
        Function<Contact,Contact> applyDate = contact -> contact.toBuilder()
                .contactDate(contactDate)
                .contactStartTime(contactDate)
                .build();

        Function<Contact,Contact> applyCaseNote = contact -> contact.toBuilder()
                .nomisCaseNoteID(caseNote.getNoteId())
                .notes(caseNote.getBody().getContent())
                .build();


        List<Function<Contact, Contact>> list = new ArrayList<Function<Contact, Contact>>() {
            {
                add(resolveContactType);
                add(resolveOffender);
                add(resolveCustodialEvent);
                add(resolveProbationArea);
                add(resolveStaff);
                add(resolveStaffEmployee);
                add(resolveTeam);
                add(applyDate);
                add(applyCaseNote);
            }
        };

        Optional<Function<Contact, Contact>> contactFunction = list.stream().reduce((x, y) -> (c) -> y.apply(x.apply(c)));

        return contactFunction.get();
    }

    @Override
    public Contact deliusContactFrom(CaseNote caseNote) {
        return contactBuilderForCaseNote(caseNote).apply(new Contact());
    }
}
