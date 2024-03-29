package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests responsible to test the validations included in the database trigger called check_storage_hierarchy
 * that prevents the creation of internal loops
 */
public class StorageUnitValidationTest extends CollectionModuleBaseIT {

  @Test
  void validate_WhenParentIsSel_ThrowsException() {
    StorageUnit storageUnit = StorageUnitFactory.newStorageUnit().uuid(UUID.randomUUID()).build();
    storageUnit.setParentStorageUnit(storageUnit);

    assertThrows(PersistenceException.class, () -> {
      //force a flush to send to db immediately
      storageUnitService.createAndFlush(storageUnit);
    });
  }

  @Test
  void validate_CyclicParent_ThrowsException() {
    StorageUnit storageUnitA = StorageUnitFactory.newStorageUnit().uuid(UUID.randomUUID()).build();
    storageUnitService.create(storageUnitA);
    
    StorageUnit storageUnitB = StorageUnitFactory.newStorageUnit().uuid(UUID.randomUUID()).build();
    storageUnitB.setParentStorageUnit(storageUnitA);
    storageUnitService.create(storageUnitB);

    storageUnitA.setParentStorageUnit(storageUnitB);

    assertThrows(PersistenceException.class,
        () -> storageUnitService.update(storageUnitA));
  }
  
}
