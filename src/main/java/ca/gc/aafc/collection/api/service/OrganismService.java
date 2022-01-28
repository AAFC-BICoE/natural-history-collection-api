package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;

import lombok.NonNull;

public class OrganismService extends DefaultDinaService<Organism> {
  
  public OrganismService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(Organism entity) {
    entity.setUuid(UUID.randomUUID());
  }

}
