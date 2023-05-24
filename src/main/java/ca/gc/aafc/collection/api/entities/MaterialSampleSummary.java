package ca.gc.aafc.collection.api.entities;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

@Entity
@Immutable
@Table(name = "material_sample_summary")
public class MaterialSampleSummary {

  @Id
  private UUID uuid;

  private String materialSampleName;

  @Type(type = "jsonb")
  private Determination effectiveDetermination;

}

