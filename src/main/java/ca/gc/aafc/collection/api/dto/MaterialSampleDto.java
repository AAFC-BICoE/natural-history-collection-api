package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@RelatedEntity(MaterialSample.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = MaterialSampleDto.TYPENAME)
@TypeName(MaterialSampleDto.TYPENAME)
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
    private PreparationTypeDto preparationType;

    @JsonApiRelation
    private MaterialSampleDto parentMaterialSample;

    @JsonApiRelation
    private List<MaterialSampleDto> materialSampleChildren;

    @JsonApiExternalRelation(type = "agent")
    @JsonApiRelation
    private ExternalRelationDto preparedBy;

    private LocalDate preparationDate;
}
