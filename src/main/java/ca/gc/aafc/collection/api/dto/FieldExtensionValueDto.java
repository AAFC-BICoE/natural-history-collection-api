package ca.gc.aafc.collection.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@JsonSerialize(using = ToStringSerializer.class)
@Data
@AllArgsConstructor
@Builder
@JsonApiResource(type = "field-extension-value")
public class FieldExtensionValueDto {
    @JsonApiId
    private final String id;
    
    private final String extensionName;
    private final String extensionKey;
    private final Field field;

}
