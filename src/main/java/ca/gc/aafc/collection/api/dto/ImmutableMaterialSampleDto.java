package ca.gc.aafc.collection.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import ca.gc.aafc.collection.api.entities.HostOrganism;
import ca.gc.aafc.collection.api.entities.MaterialSample;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * We are not setting @RelatedEntity(ImmutableMaterialSample.class) on purpose
 * The mapping is done by the Mapper
 */
@Getter
@Setter
public class ImmutableMaterialSampleDto {

  private UUID id;

  private String group;

  private MaterialSample.MaterialSampleType materialSampleType;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String dwcCatalogNumber;
  private String[] dwcOtherCatalogNumbers;

  private String materialSampleName;

  private LocalDate preparationDate;

  private Map<String, String> managedAttributes = new HashMap<>();

  private String preparationRemarks;

  private String dwcDegreeOfEstablishment;

  private String barcode;

  private Integer ordinal;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ScheduledActionDto> scheduledActions;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private HostOrganism hostOrganism;

}
