package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.PreparationProcessDefinition;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PreparationProcessDefinitionService extends DefaultDinaService<PreparationProcessDefinition> {
  public PreparationProcessDefinitionService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(PreparationProcessDefinition entity) {
    entity.setUuid(UUID.randomUUID());
  }
}
