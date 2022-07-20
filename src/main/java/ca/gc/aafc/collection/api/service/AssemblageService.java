package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class AssemblageService extends DefaultDinaService<Assemblage> {

  public AssemblageService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(Assemblage entity) {
    // allow user provided UUID
    if(entity.getUuid() == null) {
      entity.setUuid(UUID.randomUUID());
    }
  }
}
