package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity modeling a counter for a material-sample.
 * Can be used for multiple things but mostly generating material-sample children.
 */
@Entity
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
@NamedNativeQuery(
        name = "material_sample_counter_increment",
        query = "SELECT * FROM material_sample_counter_increment( :id , :amount)",
        resultSetMapping  = "IncrementFunctionOutputMapping")
@SqlResultSetMapping(
        name = "IncrementFunctionOutputMapping",
        classes = {
                @ConstructorResult(
                        targetClass = MaterialSampleCounter.IncrementFunctionOutput.class,
                        columns = {
                                @ColumnResult(name = "low_reserved_id", type = int.class),
                                @ColumnResult(name = "high_reserved_id", type = int.class)})})
public class MaterialSampleCounter implements DinaEntity {

  public static String INCREMENT_NAMED_QUERY = "material_sample_counter_increment";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  private Integer materialSampleId;

  @NotBlank
  private String counterName;

  @NotNull
  @Builder.Default
  private Integer counter = 0;

  @Override
  public String getCreatedBy() {
    return null;
  }

  @Override
  public OffsetDateTime getCreatedOn() {
    return null;
  }

  @Override
  public UUID getUuid() {
    return null;
  }

  @AllArgsConstructor
  @Getter
  public static final class IncrementFunctionOutput {
    private int lowNumber;
    private int highNumber;
  }

}
