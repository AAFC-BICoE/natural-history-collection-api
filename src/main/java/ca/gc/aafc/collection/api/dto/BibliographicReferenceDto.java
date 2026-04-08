package ca.gc.aafc.collection.api.dto;

import java.util.List;
import javax.validation.constraints.Size;

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
public class BibliographicReferenceDto {

  @Size(max = 400)
  private String title;

  private Integer year;

  @Size(max = 200)
  private String doi;

  @Size(max = 1000)
  private String bibliographicCitation;

  private List<String> author;

  private List<String> authorID;

  @Size(max = 50)
  private String volume;

  @Size(max = 50)
  private String pages;

  @Size(max = 200)
  private String journal;

  @Size(max = 1000)
  private String referenceRemarks;
}
