package uk.gov.justice.digital.noms.delius.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@ToString(exclude = "event")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "DISPOSAL")
public class Disposal {

    @Id
    @Column(name = "DISPOSAL_ID")
    private Long disposalId;

    @Column(name = "TERMINATION_DATE")
    @Temporal(TemporalType.DATE)
    private Date terminationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DISPOSAL_TYPE_ID")
    private DisposalType disposalType;

    @OneToOne()
    @JoinColumn(name = "EVENT_ID")
    private Event event;

}
