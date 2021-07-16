package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitTypeFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StorageUnitTypeServiceIT extends CollectionModuleBaseIT {

  @Autowired CacheManager cacheManager;

  @Test
  public void storageUnitTypeService_onFindOne_objectInCache() {

    StorageUnitType st = storageUnitTypeService.create(StorageUnitTypeFactory.newStorageUnitType().build());

    Cache c = cacheManager.getCache("storage_unit_type_cache");
    assertNotNull(c, "Cache exists");

    StorageUnitType stFromCache = c.get(st.getId(), StorageUnitType.class);
    assertNull(stFromCache, "Object is not in the cache");

    storageUnitTypeService.findOneById(st.getId(), StorageUnitType.class);

    stFromCache = c.get(SimpleKeyGenerator.generateKey(st.getId(), StorageUnitType.class), StorageUnitType.class);
    assertNotNull(stFromCache, "object is in the cache");
    assertEquals(st.getName(), stFromCache.getName());

  }

}
