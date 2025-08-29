package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@Data
@RelatedEntity(Protocol.class)
@JsonApiTypeForClass(ProjectDto.TYPENAME)
@TypeName(ProtocolDto.TYPENAME)
public class ProtocolDto implements JsonApiResource {

  public static final String TYPENAME = "protocol";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;
  private String protocolType;

  @JsonApiExternalRelation(type = "metadata")
  private List<ExternalRelationDto> attachments = List.of();

  private MultilingualDescription multilingualDescription;

  private List<Protocol.ProtocolData> protocolData = List.of();

  @Override
  @JsonIgnore
  public String getJsonApiType() {
    return TYPENAME;
  }

  @Override
  @JsonIgnore
  public UUID getJsonApiId() {
    return uuid;
  }
}
