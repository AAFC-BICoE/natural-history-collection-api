package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollectionService extends DefaultDinaService<Collection> {

  public CollectionService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(Collection entity) {
    entity.setUuid(UUID.randomUUID());
  }
}
