package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class StorageUnitValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private EntityManager em;

  @Test
  void validate_WhenParentIsSel_ThrowsException() {
    StorageUnit storageUnit = StorageUnitFactory.newStorageUnit().uuid(UUID.randomUUID()).build();
    storageUnit.setParentStorageUnit(storageUnit);

    assertThrows(PersistenceException.class, () -> {
      storageUnitService.create(storageUnit);
      em.flush(); //force a flush
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

    storageUnitService.update(storageUnitA);

    assertThrows(PersistenceException.class,
        () -> storageUnitService.update(storageUnitA));
  }
  
}
