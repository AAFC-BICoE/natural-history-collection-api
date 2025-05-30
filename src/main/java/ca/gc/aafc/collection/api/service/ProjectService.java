package ca.gc.aafc.collection.api.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.validation.AgentRolesValidator;
import ca.gc.aafc.collection.api.validation.ProjectExtensionValueValidator;
import ca.gc.aafc.collection.api.validation.ProjectValidator;
import ca.gc.aafc.dina.entity.AgentRoles;
import ca.gc.aafc.dina.extension.FieldExtensionValue;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.DinaEventPublisher;
import ca.gc.aafc.dina.messaging.EntityChanged;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;

@Service
public class ProjectService extends MessageProducingService<Project> {

  private final ProjectValidator projectValidator;
  private final ProjectExtensionValueValidator projectExtensionValueValidator;
  private final AgentRolesValidator agentRolesValidator;
  
  public ProjectService(
    @NonNull BaseDAO baseDAO, 
    @NonNull SmartValidator sv,
    @NonNull ProjectValidator projectValidator,
    ProjectExtensionValueValidator projectExtensionValueValidator,
    AgentRolesValidator agentRolesValidator,
    DinaEventPublisher<EntityChanged> eventPublisher
  ) {
    super(baseDAO, sv, ProjectDto.TYPENAME, eventPublisher);
    this.projectValidator = projectValidator;
    this.projectExtensionValueValidator = projectExtensionValueValidator;
    this.agentRolesValidator = agentRolesValidator;
  }

  @Override
  protected void preCreate(Project entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    entity.setGroup(standardizeGroupName(entity));
  }

  @Override
  public void validateBusinessRules(Project entity) {
    applyBusinessRule(entity, projectValidator);
    validateContributors(entity);
    validateExtensionValues(entity);
  }

  private void validateContributors(Project entity) {
    if (CollectionUtils.isEmpty(entity.getContributors())) {
      return;
    }
    for (AgentRoles ar : entity.getContributors()) {
      applyBusinessRule(ar.getAgent().toString(), ar, agentRolesValidator);
    }
  }

  private void validateExtensionValues(Project entity) {

    if (MapUtils.isNotEmpty(entity.getExtensionValues())) {
      for (String currExt : entity.getExtensionValues().keySet()) {
        entity.getExtensionValues().get(currExt).forEach((k, v) -> applyBusinessRule(
          entity.getUuid().toString(),
          FieldExtensionValue.builder().extKey(currExt).extFieldKey(k).value(v).build(),
          projectExtensionValueValidator
        ));
      }
    }
  }
}
