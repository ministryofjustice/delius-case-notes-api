package uk.gov.justice.digital.noms.delius.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "STAFF")
public class Staff {

    @Id
    @Column(name = "STAFF_ID")
    private Long staffId;

    @Column(name = "OFFICER_CODE")
    private String code;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="STAFF_TEAM",
            joinColumns = {@JoinColumn(name = "STAFF_ID")},
            inverseJoinColumns = {@JoinColumn(name = "TEAM_ID")})
    private Collection<Team> teams;
}
