package uk.gov.justice.digital.noms.delius.data.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class CaseNote {

    @NonNull
    private Long nomisId;
    @NonNull
    private Long noteId;
    @NonNull
    private CaseNoteBody body;

}
