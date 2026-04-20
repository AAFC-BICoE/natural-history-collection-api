package ca.gc.aafc.collection.api.entities;

import org.hibernate.annotations.Immutable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "storage_unit")
public class ImmutableStorageUnit extends AbstractStorageUnit {

}
