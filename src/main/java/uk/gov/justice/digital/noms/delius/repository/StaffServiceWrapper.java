package uk.gov.justice.digital.noms.delius.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.justice.digital.noms.delius.jpa.Staff;

import java.util.Optional;

@Service
public class StaffServiceWrapper implements StaffService {

    private final JpaStaffRepository staffRepository;

    @Autowired
    public StaffServiceWrapper(JpaStaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public Optional<Staff> getAppropriateStaffMemberForProbationArea(Long probationAreaId) {
        // See StaffDAOBean#getUnallocatedStaff and TeamDAOBean#getUnallocatedTeam
        return staffRepository.findByCodeLikeAndTeamsProbationAreaIdAndTeamsCodeLike(
                "%U", probationAreaId, "%UAT");
    }
}
