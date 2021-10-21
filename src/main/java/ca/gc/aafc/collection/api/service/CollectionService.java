package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;

import lombok.NonNull;

@Service
public class CollectionService extends DefaultDinaService<Collection> {

  public CollectionService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(Collection entity) {
    entity.setUuid(UUID.randomUUID());
  }
}
