package ca.gc.aafc.collection.api.dto;

import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.dto.BaseControlledVocabularyItemDto;
import ca.gc.aafc.dina.dto.RelatedEntity;

@EqualsAndHashCode(callSuper = true)
@RelatedEntity(CollectionControlledVocabularyItem.class)
@JsonApiTypeForClass(CollectionControlledVocabularyItemDto.TYPENAME)
@Data
public class CollectionControlledVocabularyItemDto extends BaseControlledVocabularyItemDto<CollectionControlledVocabularyDto> {

  private CollectionControlledVocabularyDto controlledVocabulary;

  @JsonApiId
  public UUID getUuid(){
    return uuid;
  }

  @Override
  @JsonIgnore
  public CollectionControlledVocabularyDto getControlledVocabulary() {
    return controlledVocabulary;
  }
}
