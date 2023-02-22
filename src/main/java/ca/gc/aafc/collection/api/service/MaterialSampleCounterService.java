package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.MaterialSampleCounter;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

@Service
public class MaterialSampleCounterService extends DefaultDinaService<MaterialSampleCounter> {

  public MaterialSampleCounterService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

}

