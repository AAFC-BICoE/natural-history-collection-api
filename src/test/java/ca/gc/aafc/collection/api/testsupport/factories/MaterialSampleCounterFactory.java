package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.MaterialSampleCounter;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import org.apache.commons.lang3.RandomStringUtils;

public class MaterialSampleCounterFactory implements TestableEntityFactory<MaterialSampleCounter> {

  @Override
  public MaterialSampleCounter getEntityInstance() {
    return newMaterialSampleCounter().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static MaterialSampleCounter.MaterialSampleCounterBuilder<?, ?> newMaterialSampleCounter() {
    return MaterialSampleCounter
            .builder()
            .counterName(RandomStringUtils.randomAlphabetic(5));
  }
}