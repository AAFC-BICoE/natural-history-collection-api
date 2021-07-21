package ca.gc.aafc.collection.api.dto;

import java.util.Map;

import lombok.Data;

@Data
public class VocabularyElementDto {

  private String name;
  private String term;
  private Map<String, String> labels;
  
}
