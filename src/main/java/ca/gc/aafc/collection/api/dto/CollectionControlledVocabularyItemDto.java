package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.dina.dto.BaseControlledVocabularyDto;
import ca.gc.aafc.dina.dto.BaseControlledVocabularyItemDto;

public class CollectionControlledVocabularyItemDto extends BaseControlledVocabularyItemDto {

  private CollectionControlledVocabularyDto controlledVocabulary;

  @Override
  public <T extends BaseControlledVocabularyDto> T getControlledVocabulary() {
    return (T) controlledVocabulary;
  }
}
