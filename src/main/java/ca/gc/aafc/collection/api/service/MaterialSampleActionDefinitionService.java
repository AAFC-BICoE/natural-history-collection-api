package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MaterialSampleActionDefinitionService extends DefaultDinaService<MaterialSampleActionDefinition> {
  public MaterialSampleActionDefinitionService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(MaterialSampleActionDefinition entity) {
    entity.setUuid(UUID.randomUUID());
  }
}