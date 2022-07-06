package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.repository.meta.AttributeMetaInfoProvider;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RelatedEntity(PreparationMethod.class)
@JsonApiResource(type = PreparationMethodDto.TYPENAME)
@TypeName(PreparationMethodDto.TYPENAME)
public class PreparationMethodDto extends AttributeMetaInfoProvider {

  public static final String TYPENAME = "preparation-method";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;
  
  private OffsetDateTime createdOn;
  private String createdBy;
  
  private String group;

  private String name;

  private MultilingualDescription multilingualDescription;
}
