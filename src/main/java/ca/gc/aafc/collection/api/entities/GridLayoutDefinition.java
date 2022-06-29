package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Data;
import org.javers.core.metamodel.annotation.Value;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.regex.Pattern;

@Data
@Builder
@Value // This class is considered a "value" belonging to a StorageTypeDto.
public class GridLayoutDefinition {

  @NotNull
  @Min(1)
  @Max(1000)
  private Integer numberOfRows;

  @NotNull
  @Min(1)
  @Max(1000)
  private Integer numberOfColumns;

  @NotNull
  private FillDirection fillDirection;

  public enum FillDirection {
    BY_ROW("by row"), BY_COLUMN("by column");

    private final String text;
    private static final Pattern COLUMN_REGEX = Pattern.compile("(?:by[_ ])?col(?:umn)?");
    private static final Pattern ROW_REGEX = Pattern.compile("(?:by[_ ])?row");

    FillDirection(String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }

    /**
     * Null safe method that returns the FillDirection from a string input Optional container forces
     * user to handle null values. Input string is not case-sensitive.
     *
     * @param text
     *          input string, for example, "By row"
     * @return Optional FillDirection object
     */
    public static Optional<FillDirection> fromString(String text) {
      if(ROW_REGEX.matcher(text.toLowerCase()).matches()) {
        return Optional.of(BY_ROW);
      } else if (COLUMN_REGEX.matcher(text.toLowerCase()).matches()) {
        return Optional.of(BY_COLUMN);
      }
      return Optional.empty();
    }
  }
}
