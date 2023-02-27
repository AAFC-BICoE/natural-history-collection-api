package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.service.MaterialSampleIdentifierGenerator;
import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Used to generate next identifier based on current value.
 * Identifiers are only generated, they are not reserved and not guarantee to be unique.
 */
@Repository
public class MaterialSampleIdentifierGeneratorRepository implements ResourceRepository<MaterialSampleIdentifierGeneratorDto, Serializable> {

  private static final int MAX_GENERATION_AMOUNT = 500;

  private final MaterialSampleIdentifierGenerator identifierGenerator;

  public MaterialSampleIdentifierGeneratorRepository(MaterialSampleIdentifierGenerator identifierGenerator) {
    this.identifierGenerator = identifierGenerator;
  }

  @Override
  public Class<MaterialSampleIdentifierGeneratorDto> getResourceClass() {
    return MaterialSampleIdentifierGeneratorDto.class;
  }

  @Override
  public MaterialSampleIdentifierGeneratorDto findOne(Serializable serializable, QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
  }

  @Override
  @Transactional(
    readOnly = true
  )
  public <S extends MaterialSampleIdentifierGeneratorDto> S create(S generatorDto) {

    int amount = generatorDto.getAmount() == null ? 1 : generatorDto.getAmount();

    if(amount > MAX_GENERATION_AMOUNT) {
      throw new IllegalArgumentException("over maximum amount");
    }

    List<String> nextIdentifiers = new ArrayList<>(amount);
    String lastIdentifier = identifierGenerator.generateNextIdentifier(generatorDto.getCurrentParentUUID(),
      generatorDto.getStrategy(), generatorDto.getCharacterType());
    nextIdentifiers.add(lastIdentifier);
    for (int i = 1; i < amount; i++) {
      lastIdentifier = identifierGenerator.generateNextIdentifier(lastIdentifier);
      nextIdentifiers.add(lastIdentifier);
    }
    // Id is mandatory per json:api, so we simply reuse the identifier
    generatorDto.setId("abc");
    generatorDto.setNextIdentifiers(nextIdentifiers);

    return generatorDto;
  }

  @Override
  public ResourceList<MaterialSampleIdentifierGeneratorDto> findAll(QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
  }

  @Override
  public ResourceList<MaterialSampleIdentifierGeneratorDto> findAll(Collection<Serializable> collection, QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
  }

  @Override
  public <S extends MaterialSampleIdentifierGeneratorDto> S save(S s) {
    throw new MethodNotAllowedException("PUT/PATCH");
  }

  @Override
  public void delete(Serializable serializable) {
    throw new MethodNotAllowedException("DELETE");
  }
}
