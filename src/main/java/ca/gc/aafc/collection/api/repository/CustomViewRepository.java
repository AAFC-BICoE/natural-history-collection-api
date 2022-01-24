package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.CustomViewDto;
import ca.gc.aafc.collection.api.entities.CustomView;
import ca.gc.aafc.collection.api.service.CustomViewService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomViewRepository extends DinaRepository<CustomViewDto, CustomView> {

  private final DinaAuthenticatedUser authenticatedUser;

  public CustomViewRepository(
    @NonNull CustomViewService customViewService,
    DinaAuthorizationService groupAuthorizationService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    DinaAuthenticatedUser dinaAuthenticatedUser
  ) {
    super(
        customViewService,
      groupAuthorizationService,
      Optional.empty(),
      new DinaMapper<>(CustomViewDto.class),
        CustomViewDto.class,
        CustomView.class,
      null,
      externalResourceProvider,
      buildProperties);
    this.authenticatedUser = dinaAuthenticatedUser;
  }
  @Override
  public <S extends CustomViewDto> S create(S resource) {
    if(authenticatedUser != null) {
      resource.setCreatedBy(authenticatedUser.getUsername());
    }
    return super.create(resource);
  }
  
}
