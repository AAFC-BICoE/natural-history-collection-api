package ca.gc.aafc.collection.api.repository;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.ImmutableStorageUnitChildDto;
import ca.gc.aafc.collection.api.entities.ImmutableStorageUnitChild;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;

@Repository
public class ImmutableStorageUnitChildRepository extends ReadOnlyResourceRepositoryBase<ImmutableStorageUnitChildDto, String> {

  

  protected ImmutableStorageUnitChildRepository() {
    super(ImmutableStorageUnitChildDto.class);
  }

  @Override
  public ResourceList<ImmutableStorageUnitChildDto> findAll(QuerySpec arg0) {
    // TODO Auto-generated method stub
    return null;
  }
  

}
