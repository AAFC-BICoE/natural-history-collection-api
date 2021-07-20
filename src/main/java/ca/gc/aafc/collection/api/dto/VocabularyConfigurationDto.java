package ca.gc.aafc.collection.api.dto;

import java.util.Map;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@JsonApiResource(type = "vocabulary")
public class VocabularyConfigurationDto {
  
  @JsonApiId
  private String id;
  private String name;
  private String term;
  private Map<String, String> labels;

}
