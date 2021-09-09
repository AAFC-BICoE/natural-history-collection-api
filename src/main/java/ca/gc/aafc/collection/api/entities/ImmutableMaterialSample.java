package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Immutable
@Getter
@Setter
@Table(name = "material_sample")
public class ImmutableMaterialSample extends AbstractMaterialSample {
}
