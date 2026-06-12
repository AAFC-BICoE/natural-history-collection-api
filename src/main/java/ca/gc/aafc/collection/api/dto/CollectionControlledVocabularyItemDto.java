package ca.gc.aafc.collection.api.dto;

import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

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
@TypeName(BaseControlledVocabularyItemDto.TYPENAME)
public class CollectionControlledVocabularyItemDto extends BaseControlledVocabularyItemDto<CollectionControlledVocabularyDto> {

  private CollectionControlledVocabularyDto controlledVocabulary;

  @JsonApiId
  @Id
  @PropertyName("id")
  public UUID getUuid() {
    return uuid;
  }

  @Override
  @JsonIgnore
  @ShallowReference
  public CollectionControlledVocabularyDto getControlledVocabulary() {
    return controlledVocabulary;
  }
}
