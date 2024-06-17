package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.FormTemplateDto;
import ca.gc.aafc.collection.api.entities.FormTemplate;
import ca.gc.aafc.collection.api.service.FormTemplateService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.auth.ObjectOwnerAuthorizationService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Repository
public class FormTemplateRepository extends DinaRepository<FormTemplateDto, FormTemplate> {

  private final DinaAuthenticatedUser authenticatedUser;

  public FormTemplateRepository(
    @NonNull FormTemplateService formTemplateService,
    ObjectOwnerAuthorizationService authorizationService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
    ObjectMapper objectMapper
  ) {
    super(formTemplateService,
      authorizationService,
      Optional.empty(),
      new DinaMapper<>(FormTemplateDto.class),
      FormTemplateDto.class, FormTemplate.class,
      null,
      externalResourceProvider, buildProperties, objectMapper);
    this.authenticatedUser = dinaAuthenticatedUser.orElse(null);
  }

  @Override
  public <S extends FormTemplateDto> S create(S resource) {
    if (authenticatedUser != null) {
      resource.setCreatedBy(authenticatedUser.getUsername());
    }
    return super.create(resource);
  }

  protected final void finalize() {
    // no-op
  }
}
