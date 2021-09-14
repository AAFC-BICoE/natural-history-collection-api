package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.ImmutableMaterialSample;
import ca.gc.aafc.dina.dto.HierarchicalObject;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Determination> determination;

  private String preparationRemarks;

  private String dwcDegreeOfEstablishment;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<HierarchicalObject> hierarchy;

  private String host;

  private String barcode;

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
    this.id = uuid;
  }

  public void setId(UUID id) {
    this.uuid = id;
    this.id = id;
  }
}
