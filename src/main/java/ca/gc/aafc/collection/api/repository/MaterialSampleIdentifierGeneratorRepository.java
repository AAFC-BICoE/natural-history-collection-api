package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.service.MaterialSampleIdentifierGenerator;
import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Used to generate next identifier based on current value.
 * Identifiers are only generated, they are not reserved and not guarantee to be unique.
 */
@Repository
public class MaterialSampleIdentifierGeneratorRepository implements ResourceRepository<MaterialSampleIdentifierGeneratorDto, Serializable> {

  private static final int MAX_GENERATION_QTY = 500;

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
    int quantity = generatorDto.getQuantity() == null ? 1 : generatorDto.getQuantity();
    MaterialSampleNameGeneration.CharacterType characterType = generatorDto.getCharacterType() == null ?
      MaterialSampleNameGeneration.CharacterType.LOWER_LETTER : generatorDto.getCharacterType();
    MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy = generatorDto.getStrategy() == null ?
      MaterialSampleNameGeneration.IdentifierGenerationStrategy.DIRECT_PARENT : generatorDto.getStrategy();

    // Sanity checks
    if (quantity > MAX_GENERATION_QTY) {
      throw new IllegalArgumentException("over maximum quantity");
    }

    if (generatorDto.getCurrentParentUUID() != null && CollectionUtils.isNotEmpty(generatorDto.getCurrentParentsUUID())) {
      throw new IllegalArgumentException("currentParentUUID or currentParentsUUID (list) can be provided but not both");
    }

    if (generatorDto.getStrategy() == MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED &&
      generatorDto.getMaterialSampleType() == null) {
      throw new IllegalArgumentException("materialSampleType must be provided for strategy TYPE_BASED");
    }

    if (CollectionUtils.isEmpty(generatorDto.getCurrentParentsUUID())) {
      return handleSingleParent(generatorDto, characterType, strategy, quantity);
    } else {
      return handleMultipleParents(generatorDto, characterType, strategy);
    }

  }

  private <S extends MaterialSampleIdentifierGeneratorDto> S handleSingleParent(S dto,
                                                                                MaterialSampleNameGeneration.CharacterType characterType,
                                                                                MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy,
                                                                                int qty) {
    Objects.requireNonNull(dto.getCurrentParentUUID());

    List<String> nextIdentifiers = new ArrayList<>(qty);
    String lastIdentifier = identifierGenerator.generateNextIdentifier(dto.getCurrentParentUUID(),
      strategy, dto.getMaterialSampleType(), characterType, dto.getSeparator());
    nextIdentifiers.add(lastIdentifier);
    for (int i = 1; i < qty; i++) {
      lastIdentifier = identifierGenerator.generateNextIdentifier(lastIdentifier);
      nextIdentifiers.add(lastIdentifier);
    }
    // Id is mandatory per json:api, so we simply reuse the identifier
    dto.setId(UUID.randomUUID().toString());
    dto.setNextIdentifiers(Map.of(dto.getCurrentParentUUID(), nextIdentifiers));
    return dto;
  }

  private <S extends MaterialSampleIdentifierGeneratorDto> S handleMultipleParents(S dto,
                                                                                   MaterialSampleNameGeneration.CharacterType characterType,
                                                                                   MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy) {
    Objects.requireNonNull(dto.getCurrentParentsUUID());

    List<String> nextIdentifiers = new ArrayList<>(dto.getCurrentParentsUUID().size());
    Map<UUID, List<String>> responseMap = new HashMap<>();

    for (UUID parentUUID : dto.getCurrentParentsUUID()) {
      String nextIdentifier = identifierGenerator.generateNextIdentifier(parentUUID,
        strategy, dto.getMaterialSampleType(), characterType, dto.getSeparator());

      if (!nextIdentifiers.contains(nextIdentifier)) {
        nextIdentifiers.add(nextIdentifier);
      } else {
        nextIdentifier = findNextIdentifier(nextIdentifiers, nextIdentifier);
        nextIdentifiers.add(nextIdentifier);
      }
      responseMap.put(parentUUID, List.of(nextIdentifier));
    }

    // Id is mandatory per json:api, so we simply reuse the identifier
    dto.setId(UUID.randomUUID().toString());
    dto.setNextIdentifiers(responseMap);
    return dto;
  }

  private String findNextIdentifier(List<String> nextIdentifiers, String currentIdentifier) {
    String nextIdentifier = identifierGenerator.generateNextIdentifier(currentIdentifier);

    if (!nextIdentifiers.contains(nextIdentifier)) {
      return nextIdentifier;
    }
    return findNextIdentifier(nextIdentifiers, nextIdentifier);
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
