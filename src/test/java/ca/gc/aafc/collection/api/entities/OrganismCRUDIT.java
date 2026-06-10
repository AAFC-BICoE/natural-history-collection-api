package ca.gc.aafc.collection.api.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrganismCRUDIT extends CollectionModuleBaseIT {

  @Test
  void create() {
    Organism organism = OrganismEntityFactory.newOrganism()
        .build();
    organismService.createAndFlush(organism);

    Organism result = organismService.findOne(organism.getUuid(), Organism.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(OrganismEntityFactory.GROUP, result.getGroup());

    //cleanup
    organismService.delete(organism);
  }

  @Test
  void determinedOnIsInFuture_Exception() {
    Determination determination = Determination.builder()
        .verbatimScientificName("verbatimScientificName")
        .determinedOn(LocalDate.now().plusDays(2))
        .build();

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(List.of(determination))
        .build();

    assertThrows(ValidationException.class,
        () -> organismService.createAndFlush(organism));
  }

  @Test
  void onNew_assertDefaultIsSynonymIsFalse_Passes() {
    Determination.ScientificNameSourceDetails scientificNameSourceDetails =
        Determination.ScientificNameSourceDetails.builder().build();
    assertFalse(scientificNameSourceDetails.getIsSynonym());
  }

  @Test
  void updateOrganism_WhenOnlyDeterminationIsPrimaryIsFalse_Passes() {
    Determination determination = Determination.builder()
        .verbatimScientificName("verbatimScientificName")
        .isPrimary(false)
        .build();

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(new ArrayList<>(List.of(determination)))
        .build();

    assertDoesNotThrow(() -> organismService.createAndFlush(organism));
    Organism fetchedOrganism = organismService.findOne(organism.getUuid(), Organism.class);
    assertTrue(fetchedOrganism.getDetermination().getFirst().getIsPrimary());
  }

  @Test
  void targetOrganism_multipleTargetsSameMaterialSample_Exception() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
        .isPrimary(false)
        .isFiledAs(false)
        .verbatimScientificName("verbatimScientificName")
        .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
        .isTarget(true)
        .determination(List.of(determination))
        .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
        .isTarget(true)
        .determination(List.of(determination))
        .build();
    organisms.add(organismService.createAndFlush(organism2));

    materialSample.setOrganism(organisms);

    // unique constraint will trigger an exception
    assertThrows(PersistenceException.class, () -> materialSampleService.update(materialSample));

    // Clean up
    materialSampleService.delete(materialSample);
    organisms.forEach(organism -> {
      organismService.delete(organism);
    });
  }

  @Test
  void targetOrganism_oneTargetOrganism_noExceptions() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
        .isPrimary(false)
        .isFiledAs(false)
        .verbatimScientificName("verbatimScientificName")
        .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
        .isTarget(true)
        .determination(List.of(determination))
        .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
        .isTarget(false)
        .determination(List.of(determination))
        .build();
    organisms.add(organismService.createAndFlush(organism2));

    // The material sample only gets set to the organism from the material sample service.
    materialSample.setOrganism(organisms);
    assertDoesNotThrow(() -> materialSampleService.update(materialSample));

    // Clean up
    materialSampleService.delete(materialSample);
    organisms.forEach(organism -> {
      organismService.delete(organism);
    });
  }

  @Test
  void targetOrganism_noTargetOrganism_noExceptions() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
            .isPrimary(false)
            .isFiledAs(false)
            .verbatimScientificName("verbatimScientificName")
            .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism2));

    // The material sample only gets set to the organism from the material sample service.
    materialSample.setOrganism(organisms);
    assertDoesNotThrow(() -> materialSampleService.update(materialSample));

    // Clean up
    materialSampleService.delete(materialSample);
    organisms.forEach(organism -> {
      organismService.delete(organism);
    });
  }

  @Test
  void targetOrganismNotUsed_startUsingTargetOrganism_SaveSuccess() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
            .isPrimary(false)
            .isFiledAs(false)
            .verbatimScientificName("verbatimScientificName")
            .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism2));

    // The material sample only gets set to the organism from the material sample service.
    materialSample.setOrganism(organisms);
    materialSampleService.update(materialSample);

    // now start making use of isTarget
    organism1.setIsTarget(true);
    organism2.setIsTarget(false);

    materialSampleService.update(materialSample);

    // Clean up
    materialSampleService.delete(materialSample);
    organisms.forEach(organism -> {
      organismService.delete(organism);
    });
  }
}
