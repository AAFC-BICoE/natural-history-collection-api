package ca.gc.aafc.collection.api.dto;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.HostOrganism;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.jsonapi.JsonApiImmutable;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@RelatedEntity(MaterialSample.class)
@Data
@TypeName(MaterialSampleDto.TYPENAME)
@JsonApiTypeForClass(MaterialSampleDto.TYPENAME)
public class MaterialSampleDto implements JsonApiResource {

  public static final String TYPENAME = "material-sample";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  @JsonApiImmutable(JsonApiImmutable.ImmutableOn.UPDATE)
  private int version;

  private String group;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String dwcCatalogNumber;
  private String[] dwcOtherCatalogNumbers;

  private String materialSampleName;

  private Map<String, String> identifiers = Map.of();

  private MaterialSample.MaterialSampleType materialSampleType;

  private LocalDate preparationDate;
  private String preservationType;
  private String preparationFixative;
  private String preparationMaterials;
  private String preparationSubstrate;

  private Map<String, String> managedAttributes = Map.of();
  private Map<String, String> preparationManagedAttributes = Map.of();
  private Map<String, Map<String, String>> extensionValues = Map.of();

  private String preparationRemarks;
  
  private String dwcDegreeOfEstablishment;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @DiffIgnore
  private List<MaterialSampleHierarchyObject> hierarchy;

  // calculated field
  @DiffIgnore
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String targetOrganismPrimaryScientificName;

  @DiffIgnore
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<String, String> targetOrganismPrimaryClassification;

  @DiffIgnore
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String effectiveScientificName;

  private String barcode;

  private Boolean publiclyReleasable;
  
  private String notPubliclyReleasableReason;
  private String[] tags;

  private String materialSampleState;
  private String materialSampleRemarks;
  private LocalDate stateChangedOn;
  private String stateChangeRemarks;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ScheduledActionDto> scheduledActions;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private HostOrganism hostOrganism;

  private Boolean allowDuplicateName = false;

  private Map<String, Map<String, String>> restrictionFieldsExtension = Map.of();

  private Boolean isRestricted = false;

  private String restrictionRemarks;

  private String sourceSet;

  private Boolean isBaseForSplitByType;


  // not a relationship
  @ShallowReference
  private List<AssociationDto> associations = List.of();

  // -- Relationships --
  @ShallowReference
  @JsonIgnore
  private MaterialSampleDto parentMaterialSample;

  @DiffIgnore
  @JsonIgnore
  private List<ImmutableMaterialSampleDto> materialSampleChildren;

  @ShallowReference
  @JsonIgnore
  private CollectingEventDto collectingEvent;

  @ShallowReference
  @JsonIgnore
  private CollectionDto collection;

  @ShallowReference
  @JsonIgnore
  private PreparationTypeDto preparationType;

  @ShallowReference
  @JsonIgnore
  private PreparationMethodDto preparationMethod;

  @ShallowReference
  @JsonIgnore
  private StorageUnitUsageDto storageUnitUsage;

  @ShallowReference
  @JsonIgnore
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<OrganismDto> organism = List.of();

  @ShallowReference
  @JsonIgnore
  private ProtocolDto preparationProtocol;

  @ShallowReference
  @JsonIgnore
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ProjectDto> projects = List.of();

  @ShallowReference
  @JsonIgnore
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<AssemblageDto> assemblages = List.of();

  // -- External relationships --

  @JsonApiExternalRelation(type = "person")
  @JsonIgnore
  private List<ExternalRelationDto> preparedBy = List.of();

  @JsonApiExternalRelation(type = "metadata")
  @JsonIgnore
  private List<ExternalRelationDto> attachment = List.of();

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
