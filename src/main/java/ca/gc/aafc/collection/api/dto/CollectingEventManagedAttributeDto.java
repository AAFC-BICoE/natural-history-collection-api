package ca.gc.aafc.collection.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CollectingEventManagedAttributeDto {
  private UUID attributeId;
  private String name;
  private String assignedValue;
}
