package ca.gc.aafc.collection.api.dto;

import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CitationAuthorDto implements Serializable {

  @Size(max = 200)
  @JsonProperty("given_names")
  private String givenNames;

  @Size(max = 200)
  @JsonProperty("family_names")
  private String familyNames;

  @Size(max = 200)
  private String id;
}
