package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Data;
import org.javers.core.metamodel.annotation.Value;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Value // This class is considered a "value" belonging to a CollectingEventDto.
public class GeographicThesaurus {

  public enum GeographicThesaurusSource {
    TGN
  }

  @NotNull
  private GeographicThesaurusSource source;

  @NotNull
  private String subjectId;

  @NotNull
  private String preferredTerm;

  private String preferredParent;

  private List<String> additionalParents;

}
