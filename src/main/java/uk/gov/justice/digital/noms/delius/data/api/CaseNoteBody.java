package uk.gov.justice.digital.noms.delius.data.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.joda.time.DateTime;

@Data
@Builder(toBuilder = true)
public class CaseNoteBody {
    @NonNull
    private String noteType;
    @NonNull
    private String content;
    @NonNull
    private DateTime contactTimestamp;
    @NonNull
    private DateTime systemTimestamp;
    @NonNull
    private String staffName;
    @NonNull
    private String establishmentCode;
}
