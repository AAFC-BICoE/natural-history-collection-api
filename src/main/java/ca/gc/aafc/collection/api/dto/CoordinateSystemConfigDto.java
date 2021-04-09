package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.util.List;

@Data
@JsonApiResource(type = "coordinate-system")
public class CoordinateSystemConfigDto {
  private final List<String> coordinateSystem;
}
