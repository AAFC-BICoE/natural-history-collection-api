package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.service.StorageUnitTypeService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StorageUnitTypeRepo extends DinaRepository<StorageUnitTypeDto, StorageUnitType> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public StorageUnitTypeRepo(
    @NonNull StorageUnitTypeService service,
    DinaAuthorizationService groupAuthorizationService,
    @NonNull AuditService auditService,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties buildProperties,
    ObjectMapper objectMapper
  ) {
    super(
      service,
      groupAuthorizationService,
      Optional.of(auditService),
      new DinaMapper<>(StorageUnitTypeDto.class),
      StorageUnitTypeDto.class,
      StorageUnitType.class,
      null,
      null,
      buildProperties, objectMapper);
    this.authenticatedUser = authenticatedUser;

  }

  @Override
  public <S extends StorageUnitTypeDto> S create(S resource) {
    authenticatedUser.ifPresent(user -> resource.setCreatedBy(user.getUsername()));
    return super.create(resource);
  }
}
