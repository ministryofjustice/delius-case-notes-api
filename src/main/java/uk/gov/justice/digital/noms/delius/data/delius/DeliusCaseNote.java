package uk.gov.justice.digital.noms.delius.data.delius;

import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

@Data
@Builder
public class DeliusCaseNote {

    private String nomisId;
    private Long noteId;
    private String noteType;
    private String content;
    private DateTime contactTimestamp;
    private DateTime raisedTimestamp;
    private String staffName;
    private String establishmentCode;
}
