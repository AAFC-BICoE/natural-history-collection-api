package ca.gc.aafc.collection.api.dto;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;
import org.springframework.boot.jackson.JsonComponent;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.entities.SplitConfiguration;
import ca.gc.aafc.dina.dto.RelatedEntity;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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

  @JsonDeserialize(using = SplitConfigurationDto.NoTrimJsonDeserializer.class)
  private String separator;

  private MaterialSample.MaterialSampleType materialSampleTypeCreatedBySplit;

  // this is not called
  public static class NoTrimJsonDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
      return jsonParser.getValueAsString();
    }
  }

}
