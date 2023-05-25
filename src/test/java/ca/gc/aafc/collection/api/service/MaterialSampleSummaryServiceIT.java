package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleSummary;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.DeterminationFixture;

import java.net.MalformedURLException;
import java.util.List;
import javax.inject.Inject;

public class MaterialSampleSummaryServiceIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleSummaryService materialSampleSummaryService;

  @Test
  void onMaterialSample_returnSummaryWithRightEffectiveDetermination() throws MalformedURLException {

    Organism organism1 = OrganismEntityFactory.newOrganism().isTarget(false)
      .determination(List.of(DeterminationFixture.newDeterminationBuilder()
        .verbatimScientificName("name 1").build())).build();
    organismService.createAndFlush(organism1);
    Organism organism2 = OrganismEntityFactory.newOrganism().isTarget(true)
      .determination(List.of(DeterminationFixture.newDeterminationBuilder()
        .verbatimScientificName("name 2").build())).build();
    organismService.createAndFlush(organism2);

    MaterialSample sample = MaterialSampleFactory.newMaterialSample()
      .organism(List.of(organism1, organism2))
      .build();
    materialSampleService.createAndFlush(sample);

    MaterialSample sample2 = MaterialSampleFactory.newMaterialSample()
      .parentMaterialSample(sample)
      .build();
    materialSampleService.createAndFlush(sample2);

    Assertions.assertNotNull(sample2.getUuid());

    MaterialSampleSummary msSummary = materialSampleSummaryService.findMaterialSampleSummary(sample2.getUuid());
    Assertions.assertNotNull(msSummary);
    Assertions.assertEquals("name 2", msSummary.getEffectiveDetermination().get(0).getVerbatimScientificName());
  }
}
