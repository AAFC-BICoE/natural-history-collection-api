package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.service.HierarchicalObject;
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
@RelatedEntity(StorageUnit.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = StorageUnitDto.TYPENAME)
public class StorageUnitDto {

  public static final String TYPENAME = "storage-unit";

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
  private List<StorageUnitDto> storageUnitChildren = new ArrayList<>();

  private List<HierarchicalObject> hierarchy;
}
