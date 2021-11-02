package ca.gc.aafc.collection.api.dto;

import java.util.UUID;

import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@JsonApiResource(type = CollectionSequenceGeneratorDto.TYPENAME)
public class CollectionSequenceGeneratorDto {

  public static final String TYPENAME = "collection-sequence-generator";

  @JsonApiId
  private UUID collectionId;

  private Integer amount = 1;

  private CollectionSequenceReserved result;
}
