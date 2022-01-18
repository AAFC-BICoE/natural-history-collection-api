package ca.gc.aafc.collection.api.service;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.validation.ValidationException;

import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.validation.AssociationValidator;

public class MaterialSampleServiceIT extends CollectionModuleBaseIT {

  @Inject
  private MessageSource messageSource;

  private static final String INVALID_TYPE = "not a real type";

  @Test
  void create_invalidAssociationType_exception() {
    // Create association with invalid type.
    List<Association> associations = new ArrayList<Association>();
    associations.add(Association.builder()
      .associatedSample(persistMaterialSample())
      .associationType(INVALID_TYPE)
      .build()
    );

    MaterialSample sample = MaterialSampleFactory.newMaterialSample()
      .associations(associations)
      .build();

    // Expecting a validation exception with the ASSOCIATION_TYPE_NOT_IN_VOCABULARY message.
    String errorMessage = getExpectedErrorMessage(AssociationValidator.ASSOCIATION_TYPE_NOT_IN_VOCABULARY);
    ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> materialSampleService.create(sample));
    Assertions.assertEquals(errorMessage, exception.getMessage());
  }
  
  @Test
  void create_onLinkingToAnExistingProject_secondMaterialSampleSaved() {
    Project p = ProjectFactory.newProject().build();
    projectService.create(p);

    MaterialSample sample1 = MaterialSampleFactory.newMaterialSample()
        .projects(List.of(p))
        .build();

    materialSampleService.createAndFlush(sample1);

    MaterialSample sample2 = MaterialSampleFactory.newMaterialSample()
        .projects(List.of(p))
        .build();

    materialSampleService.createAndFlush(sample2);

  }

  @Test
  void create_oneDeterminationAutomaticallySetPrimary_determinationSetPrimary() {
    Determination determination = Determination.builder()
      .isPrimary(false)
      .verbatimScientificName("verbatimScientificName")
      .build();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
        .determination(List.of(determination))
        .build();

    materialSample = materialSampleService.createAndFlush(materialSample);

    Assertions.assertNotNull(materialSample.getDetermination());
    Assertions.assertEquals(1, materialSample.getDetermination().size());
    Assertions.assertTrue(materialSample.getDetermination().get(0).getIsPrimary());
  }

  private MaterialSample persistMaterialSample() {
    MaterialSample persistMaterialSample = MaterialSampleFactory.newMaterialSample().build();
    return materialSampleService.create(persistMaterialSample);
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
