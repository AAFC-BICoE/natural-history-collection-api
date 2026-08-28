package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Dataset;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class DatasetService extends DefaultDinaService<Dataset> {

  public DatasetService(
      @NonNull BaseDAO baseDAO,
      @NonNull SmartValidator validator) {
    super(baseDAO, validator);
  }

  @Override
  protected void preCreate(Dataset entity) {
    entity.setUuid(UUID.randomUUID());
  }

}
