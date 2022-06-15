package ca.gc.aafc.collection.api.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleParentDto;
import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;

@Repository
public class MaterialSampleParentRepository extends ReadOnlyResourceRepositoryBase<MaterialSampleParentDto, UUID> {

  protected MaterialSampleParentRepository() {
    super(MaterialSampleParentDto.class);
  }

  @Override
  public MaterialSampleParentDto findOne(UUID id, QuerySpec querySpec) {
    MaterialSampleParentDto dto = new MaterialSampleParentDto();
    dto.setUuid(id);

    return dto;
  }

  @Override
  public ResourceList<MaterialSampleParentDto> findAll(QuerySpec querySpec) {
    throw new MethodNotAllowedException("method not allowed");
  }
}
