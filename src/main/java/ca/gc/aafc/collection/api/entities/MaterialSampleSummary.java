package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Immutable
@Table(name = "material_sample_summary")
@Getter
public class MaterialSampleSummary {

  @Id
  private UUID uuid;

  private String materialSampleName;

  /**
   * This is a list of determination to cover the case when there is no target organism (all primary determination will be returned).
   */
  @JdbcTypeCode(SqlTypes.JSON)
  private List<Determination> effectiveDeterminations;

}

