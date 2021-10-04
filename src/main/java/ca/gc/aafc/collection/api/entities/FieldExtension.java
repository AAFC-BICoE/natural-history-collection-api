package ca.gc.aafc.collection.api.entities;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class FieldExtension {
  
  private final String name;

  private final Map<String, String> values;

}
