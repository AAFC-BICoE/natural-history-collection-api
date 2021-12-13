package ca.gc.aafc.collection.api.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor
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
  private final String verbatimScientificName;

  @Size(max = 150)
  private final String verbatimDeterminer;

  @Size(max = 50)
  private final String verbatimDate;

  @Size(max = 250)
  private final String scientificName;

  @Size(max = 1000)
  private final String transcriberRemarks;

  @Size(max = 1000)
  private final String verbatimRemarks;

  @Size(max = 1000)
  private final String determinationRemarks;

  @Size(max = 50)
  private final String typeStatus;

  @Size(max = 250)
  private final String typeStatusEvidence;

  private final List<UUID> determiner;

  @PastOrPresent
  private final LocalDate determinedOn;

  @Size(max = 150)
  private final String qualifier;

  private final ScientificNameSource scientificNameSource;

  @Valid
  private final ScientificNameSourceDetails scientificNameDetails;

  private final Boolean isPrimary;

  private final Boolean isFileAs;

  @Builder.Default
  private final Map<String, String> managedAttributes = Map.of();

  @Getter
  @Builder
  @RequiredArgsConstructor
  public static class ScientificNameSourceDetails {
    @Size(max = 255)
    private final String labelHtml;
    @URL
    private final String sourceUrl;
    @PastOrPresent
    private final LocalDate recordedOn;
  }
}
