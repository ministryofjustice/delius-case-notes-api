package uk.gov.justice.digital.noms.delius.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;

@Data
@ToString(exclude = "staff")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TEAM")
public class Team {

    @Id
    @Column(name = "TEAM_ID")
    private Long teamId;

    @Column(name = "CODE")
    private String code;

    @Column(name = "PROBATION_AREA_ID")
    private Long probationAreaId;

    @ManyToMany(mappedBy = "teams")
    private Collection<Staff> staff;
}
