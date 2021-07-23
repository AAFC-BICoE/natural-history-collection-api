package ca.gc.aafc.collection.api.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "storage_unit")
@Immutable
// the entity will not be saved and will be silently ignored
public class ImmutableStorageUnitChild extends AbstractStorageUnit {

}
