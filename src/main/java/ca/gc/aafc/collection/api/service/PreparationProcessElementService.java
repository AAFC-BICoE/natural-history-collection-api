package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.PreparationProcessElement;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PreparationProcessElementService extends DefaultDinaService<PreparationProcessElement> {

  public PreparationProcessElementService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(PreparationProcessElement entity) {
    entity.setUuid(UUID.randomUUID());
  }
}
