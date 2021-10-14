package ca.gc.aafc.collection.api.entities;

import java.net.URI;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
public class InstitutionIdentifier {
  
  public enum IdentifierType {
    GRSCICOLL
  }

  private IdentifierType type;
  private URI uri;
}
