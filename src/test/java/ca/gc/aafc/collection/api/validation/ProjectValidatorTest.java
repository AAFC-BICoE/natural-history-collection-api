package ca.gc.aafc.collection.api.validation;

import java.time.LocalDate;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectValidatorTest extends CollectionModuleBaseIT {
  
  @Inject
  private ProjectValidator validator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_NoValidationException_ValidationSuccess() {
    Project project = ProjectFactory.newProject().build();

    Errors errors = new BeanPropertyBindingResult(project, project.getUuid().toString());
    validator.validate(project, errors);
    Assertions.assertEquals(0, errors.getAllErrors().size());
  }

  @Test 
  void validate_EndDateBeforeStartDate_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(ProjectValidator.VALID_EVENT_DATE_KEY, null);

    Project project = ProjectFactory.newProject()
      .startDate(LocalDate.of(2021, 1, 1))
      .endDate(LocalDate.of(2001, 1, 1))
      .build();

    Errors errors = new BeanPropertyBindingResult(project, project.getUuid().toString());
    validator.validate(project, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().getFirst().getDefaultMessage());
  }

  @Test
  void validate_ParentHasParent_HasErrors() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectionValidator.VALID_PARENT_HAS_NO_PARENT, "project");

    Project project = ProjectFactory.newProject().build();
    Project parentProject = ProjectFactory.newProject().build();
    Project parentParentProject = ProjectFactory.newProject().build();
    project.setParentProject(parentProject);
    parentProject.setParentProject(parentParentProject);

    Errors errors = ValidationErrorsHelper.newErrorsObject(project);

    validator.validate(project, errors);
    Assertions.assertTrue(errors.hasErrors());
    assertEquals(1, errors.getAllErrors().size());
    assertEquals(expectedErrorMessage, errors.getAllErrors().getFirst().getDefaultMessage());
  }

  private String getExpectedErrorMessage(String key, String args) {
    return messageSource.getMessage(key, args == null ? null : new String[] {args},
      LocaleContextHolder.getLocale());
  }

}
