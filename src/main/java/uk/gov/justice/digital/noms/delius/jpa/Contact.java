package uk.gov.justice.digital.noms.delius.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "CONTACT")
@SequenceGenerator(name = "CONTACT_ID_SEQ", sequenceName = "CONTACT_ID_SEQ", allocationSize = 1)
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CONTACT_ID_SEQ")
	@Column(name = "CONTACT_ID", nullable = false)
	private Long contactID;

	@Column(name = "CONTACT_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date contactDate;

	@Column(name = "CONTACT_START_TIME")
	@Temporal(TemporalType.TIME)
	private Date contactStartTime;

    @ManyToOne
    @JoinColumn(name = "contactTypeId")
    private ContactType contactType;

    @Column(name = "EVENT_ID")
	private Long eventId;

	@Lob
	@Column(name = "NOTES")
	private String notes;

	@Column(name = "OFFENDER_ID")
	private Long offenderId;

	@Column(name = "STAFF_ID")
	private Long staffId;

	@Column(name = "TEAM_ID")
	private Long teamId;

	// Y, N or Null
	@Column(name = "ALERT_ACTIVE")
	@Builder.Default private String alertActive = "N";

    @Column(name = "SENSITIVE", length = 1)
    @Builder.Default private String sensitive = "N";

	@Column(name = "STAFF_EMPLOYEE_ID")
	private Long staffEmployeeID;

	@Column(name = "PROBATION_AREA_ID")
	private Long probationAreaID;

	@Column(name = "NOMIS_CASE_NOTE_ID", unique = true)
	private Long nomisCaseNoteID;

	@Column(name = "VISOR_EXPORTED")
	@Builder.Default private String visorExported = "N";

    // Arbitrary defaults TODO:
    @Column(name = "SOFT_DELETED")
    @Builder.Default private Integer softDeleted = 0;

    @Column(name = "PARTITION_AREA_ID")
    @Builder.Default private Integer partitionAreaId = 0;

    @Column(name = "CREATED_BY_USER_ID")
    @Builder.Default private Integer createdByUserId = 0;

    @Column(name = "TRUST_PROVIDER_FLAG")
    @Builder.Default private Integer trustProviderFlag = 0;

    @Column(name = "LAST_UPDATED_USER_ID")
    @Builder.Default private Integer lastUpdateUserId = 0;

    @Column(name = "TRUST_PROVIDER_TEAM_ID")
    @Builder.Default private Integer trustProviderTeam = 0;

    @Column(name = "CREATED_DATETIME")
    @Temporal(TemporalType.TIME)
    @Builder.Default private Date createdDateTime = DateTime.now().toDate();

    @Column(name = "LAST_UPDATED_DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    @Builder.Default private Date lastUpdatedDateTime = DateTime.now().toDate();


	@Column(name = "ROW_VERSION")
	@Version
	public Long rowVersion;



}