package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.ImmutableStorageUnitChild;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.service.StorageHierarchicalObject;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@RelatedEntity(ImmutableStorageUnitChild.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = ImmutableStorageUnitChildDto.TYPENAME)
public class ImmutableStorageUnitChildDto {

  public static final String TYPENAME = "immutable-storage-unit-child";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  @JsonApiRelation
  private StorageUnitDto parentStorageUnit;

  @JsonApiRelation
  private List<ImmutableStorageUnitChildDto> storageUnitChildren = new ArrayList<>();

  private List<StorageHierarchicalObject> hierarchy;

  @JsonApiRelation
  private StorageUnitTypeDto storageUnitType;
}
