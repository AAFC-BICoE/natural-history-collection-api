package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.collection.api.service.MaterialSampleActionDefinitionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;

import lombok.NonNull;

@Repository
public class MaterialSampleActionDefinitionRepository extends DinaRepository<MaterialSampleActionDefinitionDto, MaterialSampleActionDefinition> {
  
  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

  public MaterialSampleActionDefinitionRepository(
    @NonNull MaterialSampleActionDefinitionService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
      super(
          dinaService,
          Optional.empty(),
          Optional.empty(),
          new DinaMapper<>(MaterialSampleActionDefinitionDto.class),
          MaterialSampleActionDefinitionDto.class,
          MaterialSampleActionDefinition.class,
          null,
          externalResourceProvider,
          buildProperties);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }
  @Override
  public <S extends MaterialSampleActionDefinitionDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
  
}
