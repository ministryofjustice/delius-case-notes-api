package uk.gov.justice.digital.noms.delius.data.delius;

import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

@Data
@Builder
public class DeliusCaseNote {

    private String nomisId;
    private String noteId;
    private String noteType;
    private String content;
    private DateTime timestamp;
    private String staffName;
    private String establishmentCode;
}
