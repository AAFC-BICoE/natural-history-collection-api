package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class StorageUnitTypeService extends DefaultDinaService<StorageUnitType> {

  public StorageUnitTypeService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(StorageUnitType entity) {
    entity.setUuid(UUID.randomUUID());
  }

 // @Cacheable(value = "storage_unit_type_cache", key = "#id")
  @Cacheable(value = "storage_unit_type_cache")
  @Override
  public <T> T findOneById(Object id, Class<T> entityClass) {
    return super.findOneById(id, entityClass);
  }

}
