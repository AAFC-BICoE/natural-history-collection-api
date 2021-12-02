package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.validation.ProjectValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

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
    entity.setUuid(UUID.randomUUID());
  }

  @Override
  public void validateBusinessRules(Project entity) {
    applyBusinessRule(entity, projectValidator);
  }
  
}
