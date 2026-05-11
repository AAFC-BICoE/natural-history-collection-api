package ca.gc.aafc.collection.api.dto;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CitationAuthorDto {

  @Size(max = 200)
  @JsonProperty("given_names")
  String givenNames;

  @Size(max = 200)
  @JsonProperty("family_names")
  String familyNames;

  @Size(max = 200)
  String id;
}
