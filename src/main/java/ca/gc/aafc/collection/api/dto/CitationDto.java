package ca.gc.aafc.collection.api.dto;

import java.util.List;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Data
@Builder
@RequiredArgsConstructor
@Value
// This class is considered a "value" belonging to a MaterialSample:
@org.javers.core.metamodel.annotation.Value
public class CitationDto {

  @Size(max = 400)
  private String title;

  private Integer year;

  @Size(max = 200)
  @Pattern(regexp = "(?i)^https:\\/\\/doi.org\\/10\\.\\d{4,9}\\/.+$")
  private String doi;

  private List<CitationAuthorDto> authors;

  @Size(max = 50)
  private String volume;

  @Size(max = 50)
  private String pages;

  @Size(max = 200)
  private String journal;

  @Size(max = 1000)
  private String citationRemarks;
}
