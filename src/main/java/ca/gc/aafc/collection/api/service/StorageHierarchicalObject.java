package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import ca.gc.aafc.dina.service.HierarchicalObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StorageHierarchicalObject extends HierarchicalObject {

  private UUID typeUuid;
  private String typeName;
}
