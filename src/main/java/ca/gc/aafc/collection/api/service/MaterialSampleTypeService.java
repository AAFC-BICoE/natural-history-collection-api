package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.MaterialSampleType;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class MaterialSampleTypeService extends DefaultDinaService<MaterialSampleType> {
  public MaterialSampleTypeService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(MaterialSampleType entity) {
    entity.setUuid(UUID.randomUUID());
  }
  
}
