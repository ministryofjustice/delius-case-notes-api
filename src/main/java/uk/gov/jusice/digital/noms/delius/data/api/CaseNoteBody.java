package uk.gov.jusice.digital.noms.delius.data.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.joda.time.DateTime;

@Data
@Builder
public class CaseNoteBody {
    @NonNull
    private String noteType;
    @NonNull
    private String content;
    @NonNull
    private DateTime timestamp;
    @NonNull
    private String staffName;
    @NonNull
    private String establishmentCode;
}
