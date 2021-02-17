package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.ManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import ca.gc.aafc.collection.api.service.ManagedAttributeService;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ManagedAttributeRepo extends DinaRepository<ManagedAttributeDto, ManagedAttribute> {
  public ManagedAttributeRepo(
    @NonNull ManagedAttributeService service,
    @NonNull DinaFilterResolver filterResolver,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties
  ) {
    super(
      service,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(ManagedAttributeDto.class),
      ManagedAttributeDto.class,
      ManagedAttribute.class,
      filterResolver,
      externalResourceProvider,
      buildProperties);
  }
}
