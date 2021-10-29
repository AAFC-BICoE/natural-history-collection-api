package ca.gc.aafc.collection.api.repository;

import java.io.Serializable;
import java.util.Collection;

import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;

import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;

public class CollectionSequenceRepository implements ResourceRepository<CollectionSequenceGeneratorDto, Integer> {

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
  public void delete(Integer arg0) {
    throw new MethodNotAllowedException("DELETE");
  }

  @Override
  public CollectionSequenceGeneratorDto findOne(Integer id, QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
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
  public Class<CollectionSequenceGeneratorDto> getResourceClass() {
    return CollectionSequenceGeneratorDto.class;
  }
}
