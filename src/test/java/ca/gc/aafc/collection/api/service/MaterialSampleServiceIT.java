package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;
import ca.gc.aafc.collection.api.validation.AssociationValidator;
import ca.gc.aafc.dina.testsupport.TransactionTestingHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class MaterialSampleServiceIT extends CollectionModuleBaseIT {

  @Inject
  private MessageSource messageSource;

  @Inject
  private TransactionTestingHelper transactionTestingHelper;

  private static final String INVALID_TYPE = "not a real type";

  @Test
  void create_invalidAssociationType_exception() {
    // Create association with invalid type.
    List<Association> associations = new ArrayList<>();
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
    assertEquals(errorMessage, exception.getMessage());
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
  public void hierarchy_onSetHierarchy_hierarchyLoaded() throws JsonProcessingException {

    Determination determination = DeterminationFactory.newDetermination()
            .verbatimScientificName("verbatimScientificName")
            .isPrimary(false)
            .build();
    Organism organism = OrganismEntityFactory.newOrganism()
            .isTarget(true)
            .determination(List.of(determination))
            .build();

    // we need to create the entities in another transaction so that MyBatis can see it.
    transactionTestingHelper.doInTransaction(() -> organismService.createAndFlush(organism));
    assertNotNull(organism.getUuid());

    MaterialSample parentMaterialSample = MaterialSampleFactory.newMaterialSample()
              .organism(List.of(organism))
              .build();
    transactionTestingHelper.doInTransaction(() -> materialSampleService.createAndFlush(parentMaterialSample));

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
            .parentMaterialSample(parentMaterialSample)
            .build();

    transactionTestingHelper.doInTransaction(() -> materialSampleService.createAndFlush(materialSample));

    MaterialSample freshMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);
    materialSampleService.setHierarchy(freshMaterialSample);
    assertEquals(2, freshMaterialSample.getHierarchy().size());
    assertNotNull(freshMaterialSample.getHierarchy().get(1).getOrganismPrimaryDetermination());

    //cleanup
    transactionTestingHelper.doInTransactionWithoutResult((s) -> materialSampleService.delete(materialSample));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> materialSampleService.delete(parentMaterialSample));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> organismService.delete(organism));
  }

  @Test
  public void hierarchy_onHierarchyNoTargetOrganism_hierarchyLoaded() throws JsonProcessingException {

    Determination determination = DeterminationFactory.newDetermination()
            .verbatimScientificName("verbatimScientificName")
            .isPrimary(true)
            .build();
    Organism organism = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    Determination determination2 = DeterminationFactory.newDetermination()
            .verbatimScientificName("verbatimScientificName2")
            .isPrimary(true)
            .build();
    Organism organism2 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination2))
            .build();

    // we need to create the entities in another transaction so that MyBatis can see it.
    transactionTestingHelper.doInTransaction(() -> organismService.createAndFlush(organism));
    assertNotNull(organism.getUuid());
    transactionTestingHelper.doInTransaction(() -> organismService.createAndFlush(organism2));
    assertNotNull(organism2.getUuid());

    MaterialSample parentMaterialSample = MaterialSampleFactory.newMaterialSample()
            .organism(List.of(organism, organism2))
            .build();
    transactionTestingHelper.doInTransaction(() -> materialSampleService.createAndFlush(parentMaterialSample));

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
            .parentMaterialSample(parentMaterialSample)
            .build();

    transactionTestingHelper.doInTransaction(() -> materialSampleService.createAndFlush(materialSample));

    MaterialSample freshMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);
    materialSampleService.setHierarchy(freshMaterialSample);
    assertEquals(2, freshMaterialSample.getHierarchy().size());
    assertEquals(1, freshMaterialSample.getHierarchy().get(0).getRank());
    assertTrue(StringUtils.isNotBlank(freshMaterialSample.getHierarchy().get(0).getName()));
    assertNotNull(freshMaterialSample.getHierarchy().get(1).getOrganismPrimaryDetermination());
    assertEquals(2, freshMaterialSample.getHierarchy().get(1).getOrganismPrimaryDetermination().size());

    //cleanup
    transactionTestingHelper.doInTransactionWithoutResult((s) -> materialSampleService.delete(materialSample));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> materialSampleService.delete(parentMaterialSample));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> organismService.delete(organism));
    transactionTestingHelper.doInTransactionWithoutResult((s) -> organismService.delete(organism2));
  }

  private MaterialSample persistMaterialSample() {
    MaterialSample persistMaterialSample = MaterialSampleFactory.newMaterialSample().build();
    return materialSampleService.create(persistMaterialSample);
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
