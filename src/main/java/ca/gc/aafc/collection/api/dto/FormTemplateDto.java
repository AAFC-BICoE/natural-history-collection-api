package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.FormTemplate;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RelatedEntity(FormTemplate.class)
@JsonApiTypeForClass(FormTemplateDto.TYPENAME)
public class FormTemplateDto implements JsonApiResource {

  public static final String TYPENAME = "form-template";
  
  @JsonApiId
  private UUID uuid;

  private OffsetDateTime createdOn;

  private String createdBy;

  private String name;

  private String group;

  private Boolean restrictToCreatedBy;

  private Map<String, Object> viewConfiguration;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<FormTemplate.FormComponent> components;

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
