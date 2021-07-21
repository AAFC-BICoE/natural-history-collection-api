package ca.gc.aafc.collection.api.repository;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.VocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.VocabularyDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;

@Repository
public class VocabularyRepository extends ReadOnlyResourceRepositoryBase<VocabularyDto, String> {
  
  private final VocabularyConfiguration vocabularyConfiguration;

  protected VocabularyRepository(
    @NonNull VocabularyConfiguration vocabularyConfiguration) {
    super(VocabularyDto.class);
    this.vocabularyConfiguration = vocabularyConfiguration;
  }

  @Override
  public ResourceList<VocabularyDto> findAll(QuerySpec querySpec) {
    return querySpec.apply(vocabularyConfiguration.getDegreeOfEstablishment().values());
  }
}
