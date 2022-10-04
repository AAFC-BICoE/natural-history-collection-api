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
import java.util.Collection;

@Repository
public class MaterialSampleIdentifierGeneratorRepository implements ResourceRepository<MaterialSampleIdentifierGeneratorDto, Serializable> {

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

    if(!TextHtmlSanitizer.isSafeText(serializable.toString())) {
      throw new IllegalArgumentException("unsafe value detected in attribute");
    }

    MaterialSampleIdentifierGeneratorDto dto = new MaterialSampleIdentifierGeneratorDto();
    dto.setSubmittedIdentifier(serializable.toString());
    dto.setNextIdentifier(identifierGenerator.generateNextIdentifier(serializable.toString()));
    return dto;
  }

  @Override
  public ResourceList<MaterialSampleIdentifierGeneratorDto> findAll(QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET without identifier");
  }

  @Override
  public ResourceList<MaterialSampleIdentifierGeneratorDto> findAll(Collection<Serializable> collection, QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET without identifier");
  }

  @Override
  public <S extends MaterialSampleIdentifierGeneratorDto> S save(S s) {
    throw new MethodNotAllowedException("PUT/PATCH");
  }

  @Override
  public <S extends MaterialSampleIdentifierGeneratorDto> S create(S s) {
    throw new MethodNotAllowedException("POST");
  }

  @Override
  public void delete(Serializable serializable) {
    throw new MethodNotAllowedException("DELETE");
  }
}
