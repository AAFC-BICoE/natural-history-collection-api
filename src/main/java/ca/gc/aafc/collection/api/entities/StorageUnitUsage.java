package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;

import ca.gc.aafc.dina.entity.DinaEntity;

@Entity
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
@Table
public class StorageUnitUsage implements DinaEntity {

  public static final String CELL_NUMBER_PROP_NAME = "cellNumber";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(columnDefinition = "uuid", nullable = false, unique = true)
  private UUID uuid;

  @Transient
  private String group;

  @Min(value = 1)
  @Max(value = 255)
  @Column(name = "well_column")
  private Integer wellColumn;

  @Size(max = 2)
  @Pattern(regexp = "[a-zA-Z]{1,2}")
  @Column(name = "well_row")
  private String wellRow;

  @NotEmpty
  @Size(max = 50)
  private String usageType;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  // Calculated by the service
  @Transient
  private Integer cellNumber;

  @Transient
  private String storageUnitName;

  /**
   * Only set if storageUnit is not.
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_type_id")
  private StorageUnitType storageUnitType;

  /**
   * Only set if storageUnitType is not.
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_id")
  private StorageUnit storageUnit;

  public String getGroup() {
    if (storageUnit != null) {
      return storageUnit.getGroup();
    } else if (storageUnitType != null) {
      return storageUnitType.getGroup();
    } else {
      return null;
    }
  }

  /**
   * Returns true if wellRow and wellColumn are BOTH set.
   * Coordinates might be set but invalid. Validation is still required.
   * @return
   */
  public boolean areCoordinatesSet() {
    return StringUtils.isNotBlank(wellRow) && wellColumn != null;
  }

  /**
   * Calculated field.
   * @return
   */
  public String getStorageUnitName() {
    if (storageUnit != null) {
      return storageUnit.getName();
    }
    return null;
  }

  /**
   * Returns the storageUnitType or the one from storageUnit depending which one
   * is set.
   * @return effective storageUnitType or null if none
   */
  public StorageUnitType getEffectiveStorageUnitType() {
    if (storageUnitType != null) {
      return storageUnitType;
    }

    if (storageUnit != null) {
      return storageUnit.getStorageUnitType();
    }
    return null;
  }

  public void setGroup(String group) {
    // no op
  }

  public void setStorageUnitName(String s) {
    // nop-op, read only
  }

}
