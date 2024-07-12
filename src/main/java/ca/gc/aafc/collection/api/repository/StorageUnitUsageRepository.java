package ca.gc.aafc.collection.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.entities.StorageUnitCoordinates;
import ca.gc.aafc.collection.api.service.StorageUnitCoordinatesService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.auth.GroupWithReadAuthorizationService;

import java.util.Optional;
import lombok.NonNull;

@Repository
public class StorageUnitUsageRepository extends
  DinaRepository<StorageUnitUsageDto, StorageUnitCoordinates> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public StorageUnitUsageRepository(
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
      new DinaMapper<>(StorageUnitUsageDto.class),
      StorageUnitUsageDto.class,
      StorageUnitCoordinates.class,
      null,
      externalResourceProvider,
      buildProperties, objectMapper);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends StorageUnitUsageDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
}
