package ca.gc.aafc.collection.api.dto;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.entities.SplitConfiguration;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@Data
@TypeName(SplitConfigurationDto.TYPENAME)
@RelatedEntity(SplitConfiguration.class)
@JsonApiTypeForClass(SplitConfigurationDto.TYPENAME)
public class SplitConfigurationDto implements JsonApiResource {

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

  private SplitConfiguration.Separator separator;

  private MaterialSample.MaterialSampleType materialSampleTypeCreatedBySplit;

  @JsonIgnore
  @Override
  public String getJsonApiType() {
    return TYPENAME;
  }

  @JsonIgnore
  @Override
  public UUID getJsonApiId() {
    return uuid;
  }
}
