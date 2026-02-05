package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import lombok.Data;

@Data
@RelatedEntity(Site.class)
@JsonApiTypeForClass(SiteDto.TYPENAME)
@TypeName(SiteDto.TYPENAME)
public class SiteDto implements JsonApiResource {
  public static final String TYPENAME = "site";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;
  private String group;
  private String name;
  private LocalDate startDate;
  private LocalDate endDate;
  private String code;
  private MultilingualDescription multilingualDescription;

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
