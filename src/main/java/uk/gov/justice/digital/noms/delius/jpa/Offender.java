package uk.gov.justice.digital.noms.delius.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "OFFENDER")
public class Offender {

    @Id
    @Column(name = "OFFENDER_ID")
    private Long offenderID;

    @Column(name = "NOMS_NUMBER")
    private String nomsNumber;

    @Column(name = "SOFT_DELETED")
    private boolean softDeleted;
}
