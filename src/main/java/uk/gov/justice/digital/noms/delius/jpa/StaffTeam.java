package uk.gov.justice.digital.noms.delius.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity @IdClass(StaffTeamPk.class)
@Table(name = "STAFF_TEAM")
public class StaffTeam {

    @Id
    private Long staffId;

    @Id
    private Long teamId;

}
