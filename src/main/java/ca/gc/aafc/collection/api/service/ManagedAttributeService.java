package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ManagedAttributeService extends DefaultDinaService<ManagedAttribute> {

  public ManagedAttributeService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(ManagedAttribute entity) {
    entity.setUuid(UUID.randomUUID());
  }

}
