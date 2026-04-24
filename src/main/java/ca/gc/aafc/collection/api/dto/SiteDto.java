package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Geometry;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
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
  private String code;
  private Geometry<G2D> siteGeom;

  @JsonApiExternalRelation(type = "metadata")
  @JsonIgnore
  private List<ExternalRelationDto> attachment = new ArrayList<>();

  private MultilingualDescription multilingualDescription;

  private CollectingEvent.GeographicPlaceNameSource geographicPlaceNameSource;

  private GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail;

  private String dwcCountry;

  private String dwcCountryCode;

  private String dwcStateProvince;

  private Map<String, String> managedAttributes = Map.of();

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
