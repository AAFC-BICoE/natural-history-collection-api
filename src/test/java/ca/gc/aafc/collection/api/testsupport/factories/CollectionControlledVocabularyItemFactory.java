package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.collection.api.entities.DinaComponent;
import ca.gc.aafc.collection.api.testsupport.fixtures.MultilingualTestFixture;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

public class CollectionControlledVocabularyItemFactory implements TestableEntityFactory<CollectionControlledVocabularyItem> {

  @Override
  public CollectionControlledVocabularyItem getEntityInstance() {
    return newCollectionManagedAttribute().build();
  }

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static CollectionControlledVocabularyItem.CollectionControlledVocabularyItemBuilder<?, ?> newCollectionManagedAttribute() {
    return CollectionControlledVocabularyItem
      .builder()
      .uuid(UUID.randomUUID())
      .name(RandomStringUtils.randomAlphabetic(5))
      .group(RandomStringUtils.randomAlphabetic(5))
      .createdBy(RandomStringUtils.randomAlphabetic(5))
      .vocabularyElementType(TypedVocabularyElement.VocabularyElementType.STRING)
      .acceptedValues(new String[]{"value"})
      .dinaComponent(DinaComponent.COLLECTING_EVENT.name())
      .multilingualDescription(MultilingualTestFixture.newMultilingualDescription());
  }

}
