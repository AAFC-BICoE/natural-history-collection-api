package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.javers.core.metamodel.annotation.Value;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@Value // This class is considered a "value" belonging to a Organism:
public class Determination {

  public enum ScientificNameSource {
    COLPLUS, GNA;

    @JsonCreator
    public static ScientificNameSource forValue(String v) {
      for (ScientificNameSource source : ScientificNameSource.values()) {
        if (source.name().equalsIgnoreCase(v)) {
          return source;
        }
      }
      return null;
    }
  }

  @Size(max = 250)
  private String verbatimScientificName;

  @Size(max = 150)
  private String verbatimDeterminer;

  @Size(max = 50)
  private String verbatimDate;

  @Size(max = 250)
  private String scientificName;

  @Size(max = 1000)
  private String transcriberRemarks;

  @Size(max = 1000)
  private String verbatimRemarks;

  @Size(max = 1000)
  private String determinationRemarks;

  @Size(max = 50)
  private String typeStatus;

  @Size(max = 250)
  private String typeStatusEvidence;

  private List<UUID> determiner;

  @PastOrPresent
  private LocalDate determinedOn;

  @Size(max = 150)
  private String qualifier;

  private ScientificNameSource scientificNameSource;

  @Valid
  private ScientificNameSourceDetails scientificNameDetails;

  private Boolean isPrimary;

  private Boolean isFileAs;

  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();

  @Getter
  @Builder
  @RequiredArgsConstructor
  public static class ScientificNameSourceDetails {
    @Size(max = 255)
    private final String labelHtml;
    @Size(max = 1000)
    private final String classificationPath;
    @Size(max = 1000)
    private final String classificationRanks;
    @URL
    private final String sourceUrl;
    @PastOrPresent
    private final LocalDate recordedOn;
    @Size(max = 250)
    private final String currentName;
    @Builder.Default
    private final Boolean isSynonym = false;
  }
}
