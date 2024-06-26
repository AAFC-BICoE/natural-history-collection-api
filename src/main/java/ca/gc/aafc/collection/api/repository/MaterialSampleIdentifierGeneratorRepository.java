package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
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
import java.util.UUID;

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
  @Transactional(readOnly = true)
  public <S extends MaterialSampleIdentifierGeneratorDto> S create(S generatorDto) {

    // Make sure we have sane default values
    int amount = generatorDto.getAmount() == null ? 1 : generatorDto.getAmount();
    MaterialSampleNameGeneration.CharacterType characterType = generatorDto.getCharacterType() == null ?
      MaterialSampleNameGeneration.CharacterType.LOWER_LETTER : generatorDto.getCharacterType();
    MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy = generatorDto.getStrategy() == null ?
      MaterialSampleNameGeneration.IdentifierGenerationStrategy.DIRECT_PARENT : generatorDto.getStrategy();


    if (amount > MAX_GENERATION_AMOUNT) {
      throw new IllegalArgumentException("over maximum amount");
    }

    if (generatorDto.getStrategy() == MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED &&
      generatorDto.getMaterialSampleType() == null) {
      throw new IllegalArgumentException("materialSampleType must be provided for strategy TYPE_BASED");
    }

    List<String> nextIdentifiers = new ArrayList<>(amount);
    String lastIdentifier = identifierGenerator.generateNextIdentifier(generatorDto.getCurrentParentUUID(),
      strategy, generatorDto.getMaterialSampleType(), characterType);
    nextIdentifiers.add(lastIdentifier);
    for (int i = 1; i < amount; i++) {
      lastIdentifier = identifierGenerator.generateNextIdentifier(lastIdentifier);
      nextIdentifiers.add(lastIdentifier);
    }
    // Id is mandatory per json:api, so we simply reuse the identifier
    generatorDto.setId(UUID.randomUUID().toString());
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
