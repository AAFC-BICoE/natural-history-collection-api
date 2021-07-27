package ca.gc.aafc.collection.api.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor
public class Determination {

  public enum ScientificNameSource {
    COLPLUS;

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
  private final String verbatimAgent;

  @Size(max = 50)
  private final String verbatimDate;

  @Size(max = 250)
  @NotBlank
  private final String scientificName;

  @Size(max = 50)
  private final String typeStatus;

  @Size(max = 250)
  private final String typeStatusEvidence;

  private final List<UUID> determiner;

  private final LocalDate determinedOn;

  @Size(max = 150)
  private final String qualifier;

  @NotNull
  private final ScientificNameSource scientificNameSource;

  @Size(max = 250)
  private final String scientificNameDetails;

}
