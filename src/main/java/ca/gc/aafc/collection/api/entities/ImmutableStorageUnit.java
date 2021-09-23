package ca.gc.aafc.collection.api.entities;

import org.hibernate.annotations.Immutable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Immutable
@Table(name = "storage_unit")
public class ImmutableStorageUnit extends AbstractStorageUnit {

}
