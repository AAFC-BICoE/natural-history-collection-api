package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.CollectingEventManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectingEventManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CollectingEventAttributeRepository extends DinaRepository<CollectingEventManagedAttributeDto, CollectingEventManagedAttribute> {
  public CollectingEventAttributeRepository(
    @NonNull BaseDAO baseDAO,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties
  ) {
    super(
      new DefaultDinaService<>(baseDAO) {
        @Override
        protected void preCreate(CollectingEventManagedAttribute entity) {
          entity.setUuid(UUID.randomUUID());
        }
      },
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
