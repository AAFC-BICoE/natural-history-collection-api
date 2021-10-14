package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
public class InstitutionIdentifier extends AbstractIdentifier {
  
  public enum IdentifierType {
    GRSciColl
  }

  private IdentifierType type ;
}
