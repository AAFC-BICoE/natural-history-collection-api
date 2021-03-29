package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.PreparationProcessDto;
import ca.gc.aafc.collection.api.entities.PreparationProcess;
import ca.gc.aafc.collection.api.service.PreparationProcessService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PreparationProcessRepository extends DinaRepository<PreparationProcessDto, PreparationProcess> {

  private final Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public PreparationProcessRepository(
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    @NonNull PreparationProcessService preparationProcessService,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      preparationProcessService,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(PreparationProcessDto.class),
      PreparationProcessDto.class,
      PreparationProcess.class,
      null,
      externalResourceProvider,
      buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends PreparationProcessDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }

}
