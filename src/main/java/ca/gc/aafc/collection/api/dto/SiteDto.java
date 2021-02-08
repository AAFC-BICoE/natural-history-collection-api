package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.geolatte.geom.Polygon;

import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@RelatedEntity(Site.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "site")
public class SiteDto {

  @JsonApiId
  private UUID uuid;

  private String name;
  private String description;

  private Polygon polygon;

  private String createdBy;
  private OffsetDateTime createdOn;

}
