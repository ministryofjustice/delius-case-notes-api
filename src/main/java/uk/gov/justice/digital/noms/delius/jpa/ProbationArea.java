package uk.gov.justice.digital.noms.delius.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ProbationArea {
    @Id
    @Column(name = "PROBATION_AREA_ID")
    private Long probationAreaId;

    @Column(name = "CODE")
    private String probationAreaCode;

}
