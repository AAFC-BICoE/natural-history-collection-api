package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

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
  @Type(type = "jsonb")
  private List<Determination> effectiveDetermination;

}

