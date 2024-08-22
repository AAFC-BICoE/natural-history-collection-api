package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.entities.SplitConfiguration;
import ca.gc.aafc.dina.dto.RelatedEntity;

@Data
@JsonApiResource(type = SplitConfigurationDto.TYPENAME)
@TypeName(SplitConfigurationDto.TYPENAME)
@RelatedEntity(SplitConfiguration.class)
public class SplitConfigurationDto {

  public static final String TYPENAME = "split-configuration";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;
  private String group;

  private String name;
  private MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy;

  private MaterialSample.MaterialSampleType[] conditionalOnMaterialSampleTypes;

  private MaterialSampleNameGeneration.CharacterType characterType;
  private Character separator;

  private MaterialSample.MaterialSampleType materialSampleTypeCreatedBySplit;

}
