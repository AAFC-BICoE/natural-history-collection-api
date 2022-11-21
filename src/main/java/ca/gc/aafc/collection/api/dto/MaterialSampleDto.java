package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ca.gc.aafc.collection.api.entities.ExtensionValue;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
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


  private String preparationRemarks;
  
  private String dwcDegreeOfEstablishment;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<MaterialSampleHierarchyObject> hierarchy;

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

  private List<ExtensionValue> restrictionFieldsExtension;

  private Boolean isRestricted = false;

  private String restrictionRemarks;

  // -- Relationships --

  @JsonApiRelation
  private MaterialSampleDto parentMaterialSample;

  @JsonApiRelation
  private CollectingEventDto collectingEvent;

  @JsonApiRelation
  private CollectionDto collection;

  @JsonApiRelation
  private PreparationTypeDto preparationType;

  @JsonApiRelation
  private PreparationMethodDto preparationMethod;

  @JsonApiRelation
  private StorageUnitDto storageUnit;

  @JsonApiRelation
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<OrganismDto> organism = List.of();

  @JsonApiRelation
  private AcquisitionEventDto acquisitionEvent;

  @JsonApiRelation
  private ProtocolDto preparationProtocol;

  @JsonApiRelation
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<ProjectDto> projects = List.of();

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
