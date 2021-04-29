package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.PreparationProcessElementDto;
import ca.gc.aafc.collection.api.entities.PreparationProcessElement;
import ca.gc.aafc.collection.api.service.PreparationProcessElementService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;

import lombok.NonNull;

@Repository
public class PreparationProcessElementRepository extends DinaRepository<PreparationProcessElementDto, PreparationProcessElement> {
  
  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

  public PreparationProcessElementRepository(
    @NonNull PreparationProcessElementService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
      super(
          dinaService,
          Optional.empty(),
          Optional.empty(),
          new DinaMapper<>(PreparationProcessElementDto.class),
          PreparationProcessElementDto.class,
          PreparationProcessElement.class,
          null,
          externalResourceProvider,
          buildProperties);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }
  @Override
  public <S extends PreparationProcessElementDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
}
