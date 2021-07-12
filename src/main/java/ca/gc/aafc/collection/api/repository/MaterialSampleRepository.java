package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import ca.gc.aafc.dina.security.DinaAuthorizationService;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;

@Repository
public class MaterialSampleRepository extends DinaRepository<MaterialSampleDto, MaterialSample> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public MaterialSampleRepository(
      @NonNull MaterialSampleService dinaService,
      ExternalResourceProvider externalResourceProvider,
      Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
      DinaAuthorizationService groupAuthorizationService,
      @NonNull BuildProperties buildProperties
  ) {
    super(
        dinaService,
        groupAuthorizationService,
        Optional.empty(),
        new DinaMapper<>(MaterialSampleDto.class),
        MaterialSampleDto.class,
        MaterialSample.class,
        null,
        externalResourceProvider,
        buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends MaterialSampleDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
        authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
}
