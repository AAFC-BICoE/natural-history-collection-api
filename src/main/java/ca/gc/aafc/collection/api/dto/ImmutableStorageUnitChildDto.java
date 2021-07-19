package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Id;

import org.javers.core.metamodel.annotation.PropertyName;

import ca.gc.aafc.collection.api.entities.ImmutableStorageUnitChild;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@RelatedEntity(ImmutableStorageUnitChild.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = ImmutableStorageUnitChildDto.TYPENAME)
public class ImmutableStorageUnitChildDto {

  public static final String TYPENAME = "immutable_storage_unit_child";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  @JsonApiRelation
  private StorageUnitTypeDto storageUnitType;

  @JsonApiRelation
  private StorageUnitDto parentStorageUnit;
}
