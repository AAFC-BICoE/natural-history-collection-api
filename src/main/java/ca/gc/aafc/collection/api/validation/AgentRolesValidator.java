package ca.gc.aafc.collection.api.validation;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.dina.entity.AgentRoles;
import ca.gc.aafc.dina.validation.VocabularyBasedValidator;

@Component
public class AgentRolesValidator extends VocabularyBasedValidator<AgentRoles> {

  private static final String ROLES_FIELD_NAME = "roles";

  private final List<CollectionVocabularyConfiguration.CollectionVocabularyElement>
    projectRoleVocabulary;

  AgentRolesValidator(MessageSource messageSource, CollectionVocabularyConfiguration collectionVocabularyConfiguration) {
    super(AgentRoles.class, messageSource);
    this.projectRoleVocabulary = collectionVocabularyConfiguration.getVocabularyByKey(
      CollectionVocabularyConfiguration.PROJECT_ROLE_VOCAB_KEY);
  }

  @Override
  public void validateTarget(AgentRoles target, Errors errors) {
    if (CollectionUtils.isEmpty(target.getRoles())) {
      return;
    }
    validateValuesAgainstVocabulary(target.getRoles(), ROLES_FIELD_NAME, projectRoleVocabulary,
      errors);
  }
}
