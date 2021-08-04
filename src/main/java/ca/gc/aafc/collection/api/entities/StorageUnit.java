package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.service.StorageHierarchicalObject;
import ca.gc.aafc.dina.entity.DinaEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class StorageUnit extends AbstractStorageUnit {


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_storage_unit_id")
  private StorageUnit parentStorageUnit;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_storage_unit_id", referencedColumnName = "id")
  private List<ImmutableStorageUnit> storageUnitChildren = new ArrayList<>();

  @Transient
  private List<StorageHierarchicalObject> hierarchy;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_type_id")
  private StorageUnitType storageUnitType;

}
