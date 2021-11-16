package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.HostOrganism;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.HierarchicalObject;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.CustomFieldAdapter;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.PatchStrategy;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RelatedEntity(MaterialSample.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
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

  private String group;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String dwcCatalogNumber;
  private String[] dwcOtherCatalogNumbers;

  @JsonApiRelation
  private CollectingEventDto collectingEvent;

  @JsonApiExternalRelation(type = "metadata")
  @JsonApiRelation
  private List<ExternalRelationDto> attachment = new ArrayList<>();

  private String materialSampleName;

  @JsonApiRelation
  private CollectionDto collection;

  @JsonApiRelation
  private PreparationTypeDto preparationType;

  @JsonApiRelation
  private MaterialSampleTypeDto materialSampleType;

  @JsonApiRelation
  private MaterialSampleDto parentMaterialSample;

  private List<ImmutableMaterialSampleDto> materialSampleChildren;

  @JsonApiExternalRelation(type = "person")
  @JsonApiRelation
  private ExternalRelationDto preparedBy;

  private LocalDate preparationDate;

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, String> managedAttributes = new HashMap<>();

  @JsonApiRelation
  private StorageUnitDto storageUnit;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Determination> determination;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Organism organism;

  private String preparationRemarks;
  
  private String dwcDegreeOfEstablishment;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<HierarchicalObject> hierarchy;

  private String host;

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

  private String preparationMethod;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private HostOrganism hostOrganism;

  @JsonApiExternalRelation(type = "metadata")
  @JsonApiRelation
  private List<ExternalRelationDto> preparationAttachment = new ArrayList<>();
  
  @IgnoreDinaMapping
  private List<AssociationDto> associations = new ArrayList<>();

  private Boolean allowDuplicateName = false;

  @JsonApiRelation
  private AcquisitionEventDto acquisitionEvent;

}
