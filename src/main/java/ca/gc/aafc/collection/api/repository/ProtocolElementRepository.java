package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.config.TypedVocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.ProtocolElementDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProtocolElementRepository extends ReadOnlyResourceRepositoryBase<ProtocolElementDto, String> {

  private final List<ProtocolElementDto> protocolElements;

  public ProtocolElementRepository(TypedVocabularyConfiguration typedVocabularyConfiguration) {
    super(ProtocolElementDto.class);
    protocolElements = typedVocabularyConfiguration.getProtocolDataElement()
            .stream()
            .map(entry -> new ProtocolElementDto(entry.getKey(), entry.getTerm(),
                    entry.getVocabularyElementType(), entry.getMultilingualTitle()))
            .collect(Collectors.toList());
  }

  @Override
  public ResourceList<ProtocolElementDto> findAll(QuerySpec querySpec) {
    return querySpec.apply(protocolElements);
  }
}
