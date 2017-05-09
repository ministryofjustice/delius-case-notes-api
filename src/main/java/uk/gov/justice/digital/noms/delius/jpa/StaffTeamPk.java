package uk.gov.justice.digital.noms.delius.jpa;

import javax.persistence.Column;
import java.io.Serializable;

public class StaffTeamPk implements Serializable {
    @Column(name = "TEAM_ID")
    Long teamId;

    @Column(name = "STAFF_ID")
    Long staffId;
}
