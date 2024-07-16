package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class StorageUnitUsageFactory implements TestableEntityFactory<StorageUnitUsage> {

  @Override
  public StorageUnitUsage getEntityInstance() {
    return newStorageUnitUsage().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static StorageUnitUsage.StorageUnitUsageBuilder<?, ?> newStorageUnitUsage() {
    return StorageUnitUsage
      .builder()
      .usageType("material-sample")
      .group("test group")
      .createdBy("test user");
  }
}
