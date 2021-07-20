package ca.gc.aafc.collection.api.repository;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.VocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.VocabularyConfigurationDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;

@Repository
public class VocabularyConfigurationRepository extends ReadOnlyResourceRepositoryBase<VocabularyConfigurationDto, String> {
  
  private final VocabularyConfiguration vocabularyConfiguration;

  protected VocabularyConfigurationRepository(
    @NonNull VocabularyConfiguration vocabularyConfiguration) {
    super(VocabularyConfigurationDto.class);
    this.vocabularyConfiguration = vocabularyConfiguration;
  }

  @Override
  public ResourceList<VocabularyConfigurationDto> findAll(QuerySpec querySpec) {
    return querySpec.apply(vocabularyConfiguration.getDegreeOfEstablishment());
  }
}
