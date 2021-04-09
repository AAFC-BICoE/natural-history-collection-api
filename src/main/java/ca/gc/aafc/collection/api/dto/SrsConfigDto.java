package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.util.List;

@Data
@JsonApiResource(type = "srs")
public class SrsConfigDto {
  @JsonApiId
  private final String id;
  private final List<String> srs;
}
