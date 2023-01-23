package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Immutable
@Getter
@Setter
@Table(name = "material_sample")
public class ImmutableMaterialSample extends AbstractMaterialSample {

  //The ordinal number of the entry in the list of materialSampleChildren based on order of creation
  @Transient
  @DiffIgnore
  private Integer ordinal;
}
