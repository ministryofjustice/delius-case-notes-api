package uk.gov.justice.digital.noms.delius.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.justice.digital.noms.delius.jpa.Staff;

import java.util.Optional;

public interface JpaStaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByCodeLikeAndTeamsProbationAreaIdAndTeamsCodeLike(String staffCodeLike, Long probationAreaId, String teamCodeLike);

    Optional<Staff> findByTeamsCodeLike(String teamCodeLike);

}
