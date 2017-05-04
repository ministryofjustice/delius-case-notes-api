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
@Table(name = "R_DISPOSAL_TYPE")
public class DisposalType {

    @Id
    @Column(name = "DISPOSAL_TYPE_ID")
    protected Long disposalTypeId;

    @Column(name = "SENTENCE_TYPE")
    private String sentenceType;
}
