package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.repository.meta.AttributeMetaInfoProvider;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(Institution.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = InstitutionDto.TYPENAME)
public class InstitutionDto extends AttributeMetaInfoProvider {

  public static final String TYPENAME = "institution";

  @JsonApiId
  private UUID uuid;

  private OffsetDateTime createdOn;

  private String createdBy;

  private String name;

  private MultilingualDescription multilingualDescription;
  private URL webpage;

  private String address;

  private String remarks;

}
