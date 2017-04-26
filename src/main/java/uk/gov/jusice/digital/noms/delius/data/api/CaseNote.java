package uk.gov.jusice.digital.noms.delius.data.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.joda.time.DateTime;

@Data
@Builder
public class CaseNote {

    @NonNull
    private String nomisId;
    @NonNull
    private String noteId;
    @NonNull
    private CaseNoteBody body;

}
