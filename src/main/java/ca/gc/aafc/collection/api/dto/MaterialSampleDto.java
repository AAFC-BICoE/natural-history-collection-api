package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonInclude;

import ca.gc.aafc.collection.api.entities.HostOrganism;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.PatchStrategy;
import lombok.Data;

@RelatedEntity(MaterialSample.class)
@Data
@JsonApiResource(type = MaterialSampleDto.TYPENAME)
@TypeName(MaterialSampleDto.TYPENAME)
@CustomFieldAdapter(adapters = AssociationDto.AssociationListMapperAdapter.class)
public class MaterialSampleDto {

  public static final String TYPENAME = "material-sample";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  @JsonApiField(postable = false)
  private int version;

  private String group;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String dwcCatalogNumber;
  private String[] dwcOtherCatalogNumbers;

  private String materialSampleName;

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, String> identifiers = Map.of();

  private MaterialSample.MaterialSampleType materialSampleType;

  @DiffIgnore
  private List<ImmutableMaterialSampleDto> materialSampleChildren;

  private LocalDate preparationDate;
  private String preservationType;
  private String preparationFixative;
  private String preparationMaterials;
  private String preparationSubstrate;

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, String> managedAttributes = Map.of();

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, String> preparationManagedAttributes = Map.of();

  @JsonApiField(patchStrategy = PatchStrategy.SET)
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
  
  @IgnoreDinaMapping
  private List<AssociationDto> associations = List.of();

  private Boolean allowDuplicateName = false;

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, Map<String, String>> restrictionFieldsExtension = Map.of();

  private Boolean isRestricted = false;

  private String restrictionRemarks;

  private String sourceSet;

  private Boolean isBaseForSplitByType;

  // -- Relationships --
  @ShallowReference
  @JsonApiRelation
  private MaterialSampleDto parentMaterialSample;

  @ShallowReference
  @JsonApiRelation
  private CollectingEventDto collectingEvent;

  @ShallowReference
  @JsonApiRelation
  private CollectionDto collection;

  @ShallowReference
  @JsonApiRelation
  private PreparationTypeDto preparationType;

  @ShallowReference
  @JsonApiRelation
  private PreparationMethodDto preparationMethod;

  @ShallowReference
  @JsonApiRelation
  private StorageUnitUsageDto storageUnitUsage;

  @ShallowReference
  @JsonApiRelation
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<OrganismDto> organism = List.of();

  @ShallowReference
  @JsonApiRelation
  private ProtocolDto preparationProtocol;

  @ShallowReference
  @JsonApiRelation
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ProjectDto> projects = List.of();

  @ShallowReference
  @JsonApiRelation
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<AssemblageDto> assemblages = List.of();

  // -- External relationships --

  @JsonApiExternalRelation(type = "person")
  @JsonApiRelation
  private List<ExternalRelationDto> preparedBy = List.of();

  @JsonApiExternalRelation(type = "metadata")
  @JsonApiRelation
  private List<ExternalRelationDto> attachment = List.of();

}
