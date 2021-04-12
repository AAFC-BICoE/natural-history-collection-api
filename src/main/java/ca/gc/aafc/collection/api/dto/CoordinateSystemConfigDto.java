package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.util.List;

@Data
@JsonApiResource(type = "coordinate-system")
public class CoordinateSystemConfigDto {
  @JsonApiId
  private final String id;
  private final List<String> coordinateSystem;
}
