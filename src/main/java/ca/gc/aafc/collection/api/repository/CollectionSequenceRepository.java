package ca.gc.aafc.collection.api.repository;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;

import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;

@Repository
public class CollectionSequenceRepository extends ResourceRepositoryBase<CollectionSequenceGeneratorDto, CollectionSequenceGeneratorDto> {

  public CollectionSequenceRepository() {
    super(CollectionSequenceGeneratorDto.class);
  }

  @Override
  public <S extends CollectionSequenceGeneratorDto> S create(S resource) {

    // Result collection sequence reserved.
    CollectionSequenceReserved reservedResult = new CollectionSequenceReserved();
    reservedResult.setLowReservedID(10);

    // Test
    resource.setResult(reservedResult);

    // These fields do not need to be returned back.
    resource.setAmount(null);
    resource.setCollectionId(null);

    return resource;
  }

  @Override
  public <S extends CollectionSequenceGeneratorDto> S save(S resource) {
    throw new MethodNotAllowedException("PUT/PATCH");
  }

  @Override
  public ResourceList<CollectionSequenceGeneratorDto> findAll(QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
  }

  @Override
  public ResourceList<CollectionSequenceGeneratorDto> findAll(Collection ids, QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
  }

  @Override
  public void delete(CollectionSequenceGeneratorDto entity) {
    throw new MethodNotAllowedException("DELETE");
  }

  @Override
  public Class<CollectionSequenceGeneratorDto> getResourceClass() {
    return CollectionSequenceGeneratorDto.class;
  }
}
