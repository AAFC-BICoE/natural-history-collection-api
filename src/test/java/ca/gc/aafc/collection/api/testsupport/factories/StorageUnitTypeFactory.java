package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class StorageUnitTypeFactory implements TestableEntityFactory<StorageUnitType> {

    @Override
    public StorageUnitType getEntityInstance() {
      return newStorageUnitType().build();
    }

    /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */
    public static StorageUnitType.StorageUnitTypeBuilder newStorageUnitType() {
      return StorageUnitType
          .builder()
          .group("test group")
          .createdBy("test user");
    }

  }
