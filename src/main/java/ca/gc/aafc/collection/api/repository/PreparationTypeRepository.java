package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import ca.gc.aafc.dina.security.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.collection.api.service.PreparationTypeService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;


@Repository
public class PreparationTypeRepository extends DinaRepository<PreparationTypeDto, PreparationType> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

  public PreparationTypeRepository(
    @NonNull PreparationTypeService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull AuditService auditService,
    DinaAuthorizationService dinaAdminCUDAuthorizationService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      dinaService,
      dinaAdminCUDAuthorizationService,
      Optional.of(auditService),
      new DinaMapper<>(PreparationTypeDto.class),
      PreparationTypeDto.class,
      PreparationType.class,
      null,
      externalResourceProvider,
      buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends PreparationTypeDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
  
}
