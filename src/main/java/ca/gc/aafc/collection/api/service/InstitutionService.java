package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class InstitutionService extends DefaultDinaService<Institution> {

  public InstitutionService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator validator
  ) {
    super(baseDAO, validator);
  }

  @Override
  protected void preCreate(Institution entity) {
    entity.setUuid(UUID.randomUUID());
  }
}
