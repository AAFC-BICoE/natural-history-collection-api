package ca.gc.aafc.collection.api.entities;

import java.net.URI;

import org.javers.core.metamodel.annotation.Value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
@Value
public class CollectionIdentifier {

  public enum IdentifierType {
    GRSCICOLL, INDEX_HERBARIORUM
  }

  private IdentifierType type ;
  private URI uri;
  
}
