package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.CollectingEventManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectingEventManagedAttribute;
import ca.gc.aafc.collection.api.service.CollectingEventManagedAttributeService;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CollectingEventAttributeRepository extends DinaRepository<CollectingEventManagedAttributeDto, CollectingEventManagedAttribute> {
  public CollectingEventAttributeRepository(
    @NonNull CollectingEventManagedAttributeService collectingEventManagedAttributeService,
    @NonNull BaseDAO baseDAO,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties
  ) {
    super(
      collectingEventManagedAttributeService,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(CollectingEventManagedAttributeDto.class),
      CollectingEventManagedAttributeDto.class,
      CollectingEventManagedAttribute.class,
      null,
      externalResourceProvider,
      buildProperties);
  }
}
