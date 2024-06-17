package ca.gc.aafc.collection.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.StorageUnitCoordinatesDto;
import ca.gc.aafc.collection.api.entities.StorageUnitCoordinates;
import ca.gc.aafc.collection.api.service.StorageUnitCoordinatesService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.auth.GroupWithReadAuthorizationService;

import java.util.Optional;
import lombok.NonNull;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Repository
public class StorageUnitCoordinatesRepository extends
  DinaRepository<StorageUnitCoordinatesDto, StorageUnitCoordinates> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public StorageUnitCoordinatesRepository(
    @NonNull StorageUnitCoordinatesService dinaService,
    GroupWithReadAuthorizationService groupWithReadAuthorizationService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
    ObjectMapper objectMapper
  ) {
    super(
      dinaService,
      groupWithReadAuthorizationService,
      Optional.empty(),
      new DinaMapper<>(StorageUnitCoordinatesDto.class),
      StorageUnitCoordinatesDto.class,
      StorageUnitCoordinates.class,
      null,
      externalResourceProvider,
      buildProperties, objectMapper);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends StorageUnitCoordinatesDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }

  protected final void finalize() {
    // no-op
  }

}

