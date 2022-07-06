package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.FormTemplate;
import ca.gc.aafc.dina.dto.RelatedEntity;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.PatchStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RelatedEntity(FormTemplate.class)
@JsonApiResource(type = FormTemplateDto.TYPENAME)
public class FormTemplateDto {

  public static final String TYPENAME = "form-template";
  
  @JsonApiId
  private UUID uuid;

  private OffsetDateTime createdOn;

  private String createdBy;

  private String name;

  private String group;

  private Boolean restrictToCreatedBy;

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<String, Object> viewConfiguration;
}
