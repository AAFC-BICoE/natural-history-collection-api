package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.FormTemplate;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class FormTemplateService extends DefaultDinaService<FormTemplate> {
  public FormTemplateService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(FormTemplate entity) {
    entity.setUuid(UUID.randomUUID());
  }

}
