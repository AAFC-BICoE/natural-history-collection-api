package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.service.MaterialSampleIdentifierGenerator;
import ca.gc.aafc.dina.security.TextHtmlSanitizer;
import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepository;
import io.crnk.core.resource.list.ResourceList;
import org.springframework.stereotype.Repository;

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
  public <S extends MaterialSampleIdentifierGeneratorDto> S create(S generatorDto) {
    if(!TextHtmlSanitizer.isSafeText(generatorDto.getSubmittedIdentifier())) {
      throw new IllegalArgumentException("unsafe value detected in attribute");
    }

    List<String> nextIdentifiers = new ArrayList<>();
    int amount = generatorDto.getAmount() == null ? 1 : generatorDto.getAmount();

    if(amount > MAX_GENERATION_AMOUNT) {
      throw new IllegalArgumentException("over maximum amount");
    }

    String lastIdentifier = generatorDto.getSubmittedIdentifier();
    for (int i = 0; i < amount; i++) {
      lastIdentifier = identifierGenerator.generateNextIdentifier(lastIdentifier);
      nextIdentifiers.add(lastIdentifier);
    }

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
