package uk.gov.justice.digital.noms.delius.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.justice.digital.noms.delius.data.api.CaseNote;
import uk.gov.justice.digital.noms.delius.jpa.Contact;
import uk.gov.justice.digital.noms.delius.jpa.ContactType;
import uk.gov.justice.digital.noms.delius.jpa.Event;
import uk.gov.justice.digital.noms.delius.jpa.Offender;
import uk.gov.justice.digital.noms.delius.jpa.ProbationArea;
import uk.gov.justice.digital.noms.delius.jpa.Staff;
import uk.gov.justice.digital.noms.delius.jpa.Team;
import uk.gov.justice.digital.noms.delius.repository.CustodialEventsService;
import uk.gov.justice.digital.noms.delius.repository.JpaContactTypeRepository;
import uk.gov.justice.digital.noms.delius.repository.JpaOffenderRepository;
import uk.gov.justice.digital.noms.delius.repository.JpaProbationAreaRepository;
import uk.gov.justice.digital.noms.delius.repository.StaffService;

import java.util.Date;
import java.util.Optional;

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

    private static <T> T getOrThrow(Optional<T> value, String exception) {

        return value.orElseThrow(() -> new IllegalArgumentException(exception));
    }

    private Contact.ContactBuilder contactBuilderForCaseNote(CaseNote caseNote) {

        String nomisId = caseNote.getNomisId().toString();
        String noteType = caseNote.getBody().getNoteType();
        Date contactDate = caseNote.getBody().getContactTimestamp().toDate();
        String estCode = caseNote.getBody().getEstablishmentCode();

        ContactType contactType = getOrThrow(contactTypeRepository.findByNomisContactType(noteType),
                "No Delius contact type found for nomis contact type '" + noteType + "'");

        Long offenderId = getOrThrow(offenderRepository.findByNomsNumber(nomisId).map(Offender::getOffenderID),
                "No Delius offender found for nomis id '" + nomisId + "'");

        Long eventId = getOrThrow(custodialEventsService.currentCustodialEvent(offenderId).map(Event::getEventID),
                "No current custodial event for offender id '" + offenderId + "'" );

        Long areaId = getOrThrow(probationAreaRepository.findByProbationAreaCode(estCode).map(ProbationArea::getProbationAreaId),
                "No probation area found for nomis establishmentCode '" + estCode + "'");

        Optional<Staff> staff = staffService.getAppropriateStaffMemberForProbationArea(areaId);

        Long staffId = getOrThrow(staff.map(Staff::getStaffId),
                "Could not resolve appropriate delius Staff for delius probationAreaId '" + areaId + "'");

        Long teamId = getOrThrow(staff.flatMap(s -> s.getTeams().stream().map(Team::getTeamId).findFirst()),
                "Could not resolve appropriate delius Team for delius probationAreaId '" + areaId + "'" );

        return Contact.builder().
                contactType(contactType).
                offenderId(offenderId).
                eventId(eventId).
                probationAreaID(areaId).
                staffId(staffId).
                staffEmployeeID(staffId).
                teamId(teamId).
                contactDate(contactDate).
                contactStartTime(contactDate).
                lastUpdatedDateTime(caseNote.getBody().getRaisedTimestamp().toDate()).
                nomisCaseNoteID(caseNote.getNoteId()).
                notes(caseNote.getBody().getContent());
    }

    @Override
    public Contact deliusContactFrom(CaseNote caseNote) {
        return contactBuilderForCaseNote(caseNote).build();
    }
}

/*
    private static <T> Function<T, T> reduceFunctions(List<Function<T, T>> list) {

        return list.stream().reduce(Function::compose).orElseGet(Function::identity);
    }
*/
