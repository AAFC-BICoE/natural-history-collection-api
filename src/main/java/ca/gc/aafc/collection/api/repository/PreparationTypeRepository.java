package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.service.DinaAuthorizationService;
import ca.gc.aafc.dina.service.DinaService;
import lombok.NonNull;


@Repository
public class PreparationTypeRepository extends DinaRepository<PreparationTypeDto, PreparationType> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

  public PreparationTypeRepository(
    @NonNull PreparationTypeService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
      super(
          dinaService,
          Optional.empty(),
          Optional.empty(),
          new DinaMapper<>(PreparationTypeDto.class),
          PreparationTypeDto.class,
          PreparationType.class,
          null,
          externalResourceProvider,
          buildProperties);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;
  
}
