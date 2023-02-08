package ca.gc.aafc.collection.api.testsupport.factories;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class StorageUnitFactory implements TestableEntityFactory<StorageUnit> {

    @Override
    public StorageUnit getEntityInstance() {
      return newStorageUnit().build();
    }

    /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */
    public static StorageUnit.StorageUnitBuilder<?, ?> newStorageUnit() {
      return StorageUnit
          .builder()
          .group("test group")
          .barcode("ABC-928723619")
          .name(RandomStringUtils.randomAlphabetic(4))
          .createdBy("test user");
    }

  }
