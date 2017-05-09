package uk.gov.justice.digital.noms.delius.repository;

import uk.gov.justice.digital.noms.delius.jpa.Staff;

import java.util.Optional;

public interface StaffService {

    Optional<Staff> getAppropriateStaffMemberForProbationArea(Long probationAreaId);
}
