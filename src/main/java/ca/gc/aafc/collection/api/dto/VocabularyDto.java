package ca.gc.aafc.collection.api.dto;

import java.util.List;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@JsonApiResource(type = "vocabulary")
public class VocabularyDto {
  
  @JsonApiId
  private String id;

  private List<VocabularyElementDto> vocabularyElements;

}
