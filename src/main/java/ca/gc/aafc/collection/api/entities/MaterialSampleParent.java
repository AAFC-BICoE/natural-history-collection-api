package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Immutable entity representing the parent of a material sample.
 * It only contains the object internal identity (id) and external identify (uuid)
 */
@Entity
@Table(name = "material_sample")
@Immutable
@Getter
@Setter
public class MaterialSampleParent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

}
