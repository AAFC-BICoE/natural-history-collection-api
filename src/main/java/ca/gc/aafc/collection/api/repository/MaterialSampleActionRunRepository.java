package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleActionRunDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionRun;
import ca.gc.aafc.collection.api.service.MaterialSampleActionRunService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;

@Repository
public class MaterialSampleActionRunRepository extends DinaRepository<MaterialSampleActionRunDto, MaterialSampleActionRun> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

  public MaterialSampleActionRunRepository(
    @NonNull MaterialSampleActionRunService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
      super(
          dinaService,
          Optional.empty(),
          Optional.empty(),
          new DinaMapper<>(MaterialSampleActionRunDto.class),
          MaterialSampleActionRunDto.class,
          MaterialSampleActionRun.class,
          null,
          externalResourceProvider,
          buildProperties);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }
  @Override
  public <S extends MaterialSampleActionRunDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
  
}
