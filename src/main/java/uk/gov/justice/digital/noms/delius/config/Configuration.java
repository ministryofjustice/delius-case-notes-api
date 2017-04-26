package uk.gov.justice.digital.noms.delius.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import uk.gov.justice.digital.noms.delius.repository.CaseNoteRepository;
import uk.gov.justice.digital.noms.delius.repository.Repository;
import uk.gov.justice.digital.noms.delius.service.CaseNotesService;
import uk.gov.justice.digital.noms.delius.service.Service;

import java.util.Optional;

public class Configuration extends AbstractModule {

    @Override
    protected void configure() {

        bind(Service.class).to(CaseNotesService.class);
        bind(Repository.class).to(CaseNoteRepository.class);

        bind(Integer.class).annotatedWith(Names.named("port")).toInstance(Integer.valueOf(Optional.ofNullable(System.getenv("PORT")).orElse("8080")));
    }

}
