package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleCounter;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleCounterFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MaterialSampleCounterServiceIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleService materialSampleService;

  @Inject
  private MaterialSampleCounterService materialSampleCounterService;

  @Test
  public void onValidMaterialSampleCounter_recordSaved() {

    //Create a material-sample
    MaterialSample ms = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.create(ms);

    MaterialSampleCounter msCounter = MaterialSampleCounterFactory.newMaterialSampleCounter()
            .materialSampleId(ms.getId())
            .build();
    materialSampleCounterService.create(msCounter);

    assertNotNull(msCounter.getId());
  }


}
