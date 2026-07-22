package ca.gc.aafc.collection.api.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.Serializable;
import java.util.UUID;

import ca.gc.aafc.dina.dto.HierarchicalObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StorageHierarchicalObject implements Serializable {

  @SuppressFBWarnings(
    value = "SE_BAD_FIELD",
    justification = "this field is not serialized by Hibernate. Will inherit Serializable in dina-base 0.175"
  )
  @JsonUnwrapped
  private HierarchicalObject hierarchicalObject;
  private UUID typeUuid;
  private String typeName;
}
