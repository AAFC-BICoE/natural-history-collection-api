package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleSummary;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;

import javax.inject.Inject;

public class MaterialSampleSummaryServiceIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleSummaryService materialSampleSummaryService;

  @Test
  void onMaterialSample_returnSummary() {
    MaterialSample sample = MaterialSampleFactory.newMaterialSample()
      .build();
    materialSampleService.createAndFlush(sample);

    Assertions.assertNotNull(sample.getUuid());

    MaterialSampleSummary msSummary = materialSampleSummaryService.findMaterialSampleSummary(sample.getUuid());
    Assertions.assertNotNull(msSummary);
  }
}
