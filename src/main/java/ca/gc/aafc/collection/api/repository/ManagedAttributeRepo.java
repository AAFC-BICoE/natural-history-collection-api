package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.ManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import ca.gc.aafc.collection.api.service.ManagedAttributeService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ManagedAttributeRepo extends DinaRepository<ManagedAttributeDto, ManagedAttribute> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public ManagedAttributeRepo(
    @NonNull ManagedAttributeService service,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      service,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(ManagedAttributeDto.class),
      ManagedAttributeDto.class,
      ManagedAttribute.class,
      null,
      externalResourceProvider,
      buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends ManagedAttributeDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }

}
