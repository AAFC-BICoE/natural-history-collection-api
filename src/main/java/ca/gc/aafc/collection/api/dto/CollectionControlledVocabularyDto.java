package ca.gc.aafc.collection.api.dto;

import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.CollectionControlledVocabulary;
import ca.gc.aafc.dina.dto.BaseControlledVocabularyDto;
import ca.gc.aafc.dina.dto.RelatedEntity;

@EqualsAndHashCode(callSuper = true)
@RelatedEntity(CollectionControlledVocabulary.class)
@JsonApiTypeForClass(BaseControlledVocabularyDto.TYPENAME)
@Data
@TypeName(BaseControlledVocabularyDto.TYPENAME)
public class CollectionControlledVocabularyDto extends BaseControlledVocabularyDto {

  @JsonApiId
  @Id
  @PropertyName("id")
  public UUID getUuid() {
    return uuid;
  }

}
