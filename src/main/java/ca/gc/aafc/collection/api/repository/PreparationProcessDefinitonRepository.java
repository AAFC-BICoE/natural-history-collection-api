package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.PreparationProcessDefinitionDto;
import ca.gc.aafc.collection.api.entities.PreparationProcessDefinition;
import ca.gc.aafc.collection.api.service.PreparationProcessDefinitionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;

import lombok.NonNull;


public class PreparationProcessDefinitonRepository extends DinaRepository<PreparationProcessDefinitionDto, PreparationProcessDefinition> {
  
  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

  public PreparationProcessDefinitonRepository(
    @NonNull PreparationProcessDefinitionService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
      super(
          dinaService,
          Optional.empty(),
          Optional.empty(),
          new DinaMapper<>(PreparationProcessDefinitionDto.class),
          PreparationProcessDefinitionDto.class,
          PreparationProcessDefinition.class,
          null,
          externalResourceProvider,
          buildProperties);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }
  @Override
  public <S extends PreparationProcessDefinitionDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
  
}
