package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.validation.SmartValidator;

@Service
public class CollectorGroupService extends DefaultDinaService<CollectorGroup> {

  public CollectorGroupService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(CollectorGroup entity) {
    entity.setUuid(UUID.randomUUID());
  }

}
