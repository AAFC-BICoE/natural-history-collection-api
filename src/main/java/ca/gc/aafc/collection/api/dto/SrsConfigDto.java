package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.util.List;

@Data
@JsonApiResource(type = "srs")
public class SrsConfigDto {
  private final List<String> srs;
}
