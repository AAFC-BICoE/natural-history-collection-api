package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.repository.meta.AttributeMetaInfoProvider;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import io.crnk.core.resource.annotations.JsonApiRelation;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@Data
@RelatedEntity(Protocol.class)
@JsonApiResource(type = ProtocolDto.TYPENAME)
@TypeName(ProtocolDto.TYPENAME)
public class ProtocolDto extends AttributeMetaInfoProvider {

  public static final String TYPENAME = "protocol";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  @JsonApiExternalRelation(type = "metadata")
  @JsonApiRelation
  private List<ExternalRelationDto> attachments = List.of();

  private MultilingualDescription multilingualDescription;
}
