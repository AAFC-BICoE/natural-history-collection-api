package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleTypeDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleType;
import ca.gc.aafc.collection.api.service.MaterialSampleTypeService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.DinaAuthorizationService;
import lombok.NonNull;


@Repository
public class MaterialSampleTypeRepository extends DinaRepository<MaterialSampleTypeDto, MaterialSampleType> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

  public MaterialSampleTypeRepository(
    @NonNull MaterialSampleTypeService dinaService,
    ExternalResourceProvider externalResourceProvider,
    Optional<DinaAuthorizationService> groupAuthorizationService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
      super(
          dinaService,
          groupAuthorizationService,
          Optional.empty(),
          new DinaMapper<>(MaterialSampleTypeDto.class),
          MaterialSampleTypeDto.class,
          MaterialSampleType.class,
          null,
          externalResourceProvider,
          buildProperties);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends MaterialSampleTypeDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
  
}
