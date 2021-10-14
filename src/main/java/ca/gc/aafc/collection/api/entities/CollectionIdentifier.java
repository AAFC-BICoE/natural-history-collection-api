package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
public class CollectionIdentifier {

  public enum IdentifierType {
    GRSCICOLL, INDEX_HERBARIORUM
  }

  private IdentifierType type ;
  private URI uri;
  
}
