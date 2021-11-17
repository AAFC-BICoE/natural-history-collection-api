package ca.gc.aafc.collection.api.dto;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;
import ca.gc.aafc.dina.dto.RelatedEntity;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@JsonApiResource(type = CollectionSequenceGeneratorDto.TYPENAME)
@RelatedEntity(CollectionSequenceGenerationRequest.class)
public class CollectionSequenceGeneratorDto {

  public static final String TYPENAME = "collection-sequence-generator";

  @JsonApiId
  private UUID collectionId;

  private Integer amount = 1;

  @JsonApiField(postable = false)
  private CollectionSequenceReserved result;

  // Retrieved from the collection.
  @JsonApiField(postable = false)
  private String group;
}
