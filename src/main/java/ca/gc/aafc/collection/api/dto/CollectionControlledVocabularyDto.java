package ca.gc.aafc.collection.api.dto;

import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.CollectionControlledVocabulary;
import ca.gc.aafc.dina.dto.BaseControlledVocabularyDto;
import ca.gc.aafc.dina.dto.RelatedEntity;

@EqualsAndHashCode(callSuper = true)
@RelatedEntity(CollectionControlledVocabulary.class)
@JsonApiTypeForClass(BaseControlledVocabularyDto.TYPENAME)
@Data
public class CollectionControlledVocabularyDto extends BaseControlledVocabularyDto {

  @JsonApiId
  public UUID getUuid() {
    return uuid;
  }

}
