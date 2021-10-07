package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.MaterialSampleActionRun;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class MaterialSampleActionRunFactory implements TestableEntityFactory<MaterialSampleActionRun> {

  @Override
  public MaterialSampleActionRun getEntityInstance() {
    return newMaterialSampleActionRun().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static MaterialSampleActionRun.MaterialSampleActionRunBuilder newMaterialSampleActionRun() {
    return MaterialSampleActionRun
        .builder()
        .uuid(UUID.randomUUID())
        .createdBy("test user");
  }
  
}
