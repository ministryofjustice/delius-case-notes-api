package uk.gov.justice.digital.noms.delius.jpa;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Data
@Entity
@Table(name = "R_CONTACT_TYPE")
public class ContactType {

    @Id
    @Column(name = "CONTACT_TYPE_ID")
    private Long contactTypeID;

    @Column(name = "NOMIS_CONTACT_TYPE")
    private String nomisContactType;

}