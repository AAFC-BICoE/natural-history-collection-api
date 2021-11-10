package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.AcquisitionEventDto;
import ca.gc.aafc.collection.api.entities.AcquisitionEvent;
import ca.gc.aafc.collection.api.service.AcquisitionEventService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import lombok.NonNull;

@Repository
public class AcquisitionEventRepository extends DinaRepository<AcquisitionEventDto, AcquisitionEvent> {
  
  private final DinaAuthenticatedUser dinaAuthenticatedUser;

  public AcquisitionEventRepository(
    @NonNull AcquisitionEventService dinaService,
    DinaAuthorizationService groupAuthorizationService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      dinaService,
      groupAuthorizationService,
      Optional.empty(),
      new DinaMapper<>(AcquisitionEventDto.class),
      AcquisitionEventDto.class,
      AcquisitionEvent.class,
      null,
      externalResourceProvider,
      buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser.orElse(null);
  }

  @Override
  public <S extends AcquisitionEventDto> S create(S resource) {
    if (dinaAuthenticatedUser != null) {
      resource.setCreatedBy(dinaAuthenticatedUser.getUsername());
    }
    return super.create(resource);
  }
}
