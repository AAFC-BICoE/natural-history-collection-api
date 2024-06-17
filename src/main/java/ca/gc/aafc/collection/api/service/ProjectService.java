package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.validation.ProjectValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Service
public class ProjectService extends DefaultDinaService<Project> {

  private final ProjectValidator projectValidator;
  
  public ProjectService(
    @NonNull BaseDAO baseDAO, 
    @NonNull SmartValidator sv,
    @NonNull ProjectValidator projectValidator
  ) {
    super(baseDAO, sv);
    this.projectValidator = projectValidator;
  }

  @Override
  protected void preCreate(Project entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
  }

  @Override
  public void validateBusinessRules(Project entity) {
    applyBusinessRule(entity, projectValidator);
  }

  protected final void finalize() {
    // no-op
  }
}
