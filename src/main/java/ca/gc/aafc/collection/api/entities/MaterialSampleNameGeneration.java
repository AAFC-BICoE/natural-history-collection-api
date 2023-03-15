package ca.gc.aafc.collection.api.entities;

/**
 * Not a real entity. Just providing common structures related to MaterialSampleNameGeneration.
 */
public class MaterialSampleNameGeneration {
  public enum IdentifierGenerationStrategy { TYPE_BASED, DIRECT_PARENT }

  public enum CharacterType { NUMBER, LOWER_LETTER, UPPER_LETTER }
}
