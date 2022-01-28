package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.HostOrganism;
import ca.gc.aafc.collection.api.entities.ImmutableMaterialSample;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.PatchStrategy;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RelatedEntity(ImmutableMaterialSample.class)
@Getter
@Setter
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ImmutableMaterialSampleDto {

  @JsonApiId
  @JsonIgnore
  private UUID uuid;

  @IgnoreDinaMapping
  private UUID id;

  private String group;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String dwcCatalogNumber;
  private String[] dwcOtherCatalogNumbers;

  private String materialSampleName;

  private LocalDate preparationDate;

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, String> managedAttributes = new HashMap<>();

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<Organism> organism;

  private String preparationRemarks;

  private String dwcDegreeOfEstablishment;

  private String host;

  private String barcode;

  private String filedAs;

  private String preparationMethod;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ScheduledActionDto> scheduledActions;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private HostOrganism hostOrganism;

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
    this.id = uuid;
  }

  public void setId(UUID id) {
    this.uuid = id;
    this.id = id;
  }
}
