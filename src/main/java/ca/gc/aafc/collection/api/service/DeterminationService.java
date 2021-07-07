package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class DeterminationService extends DefaultDinaService<Determination> {

  public DeterminationService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator validator
  ) {
    super(baseDAO, validator);
  }

  @Override
  protected void preCreate(Determination entity) {
    entity.setUuid(UUID.randomUUID());
  }
}
