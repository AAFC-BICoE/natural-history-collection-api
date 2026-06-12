package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabulary;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionControlledVocabularyItemFactory;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

public class CollectingEventServiceIT extends CollectionModuleBaseIT {

  private CollectionControlledVocabulary getManagedAttributeControlledVocabularyRef() {
    return collectionControlledVocabularyService.getReferenceByNaturalId(
      CollectionControlledVocabulary.class,
      CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID
    );
  }

  @Test
  public void geographicData_onNoneProvided_valuesFromGeographicPlaceNameSourceDetail() {
    GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = CollectingEventFactory.newGeographicPlaceNameSourceDetail();

    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .dwcCountry(null)
      .dwcCountryCode(null)
      .dwcStateProvince(null)
      .geographicPlaceNameSource(CollectingEventFactory.GEOGRAPHIC_PLACE_NAME_SOURCE)
      .geographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail)
      .build();
    collectingEventService.createAndFlush(collectingEvent);

    CollectingEvent collectingEventReloaded = collectingEventService.findOne(collectingEvent.getUuid(), CollectingEvent.class);

    assertEquals(CollectingEventFactory.TEST_COUNTRY.getCode(), collectingEventReloaded.getDwcCountryCode());
    assertEquals(CollectingEventFactory.TEST_COUNTRY.getName(), collectingEventReloaded.getDwcCountry());
    assertEquals(CollectingEventFactory.TEST_PROVINCE.getName(), collectingEventReloaded.getDwcStateProvince());
  }

  @Test
  public void geographicData_ifProvided_overwrittenByPlaceNameSourceDetail() {
    GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = CollectingEventFactory.newGeographicPlaceNameSourceDetail();

    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .dwcCountry("Arctic")
      .dwcCountryCode("AA")
      .dwcStateProvince("province")
      .geographicPlaceNameSource(CollectingEventFactory.GEOGRAPHIC_PLACE_NAME_SOURCE)
      .geographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail)
      .build();
    collectingEventService.createAndFlush(collectingEvent);

    CollectingEvent collectingEventReloaded = collectingEventService.findOne(collectingEvent.getUuid(), CollectingEvent.class);

    assertEquals(CollectingEventFactory.TEST_COUNTRY.getCode(), collectingEventReloaded.getDwcCountryCode());
    assertEquals(CollectingEventFactory.TEST_COUNTRY.getName(), collectingEventReloaded.getDwcCountry());
    assertEquals(CollectingEventFactory.TEST_PROVINCE.getName(), collectingEventReloaded.getDwcStateProvince());
  }

  @Test
  void validate_managedAttribute_WhenValidStringType() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(null)
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Map<String, String> maMap = new HashMap<>();
    maMap.put(testManagedAttribute.getKey(), "anything");

    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
        .managedAttributes(maMap)
      .build();
    assertDoesNotThrow(() -> collectingEventService.create(collectingEvent));

    assertThrows(IllegalStateException.class,
      () -> collectionControlledVocabularyItemService.delete(testManagedAttribute));
  }

  @Test
  void validate_WhenInvalidIntegerTypeExceptionThrown() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(null)
        .vocabularyElementType(TypedVocabularyElement.VocabularyElementType.INTEGER)
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Map<String, String> maMap = new HashMap<>();
    maMap.put(testManagedAttribute.getKey(), "1.2");

    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .managedAttributes(maMap)
      .build();
    assertThrows(ValidationException.class, () ->  collectingEventService.update(collectingEvent));
    collectionControlledVocabularyItemService.delete(testManagedAttribute);
  }

  @Test
  void assignedValueContainedInAcceptedValues_validationPasses() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(new String[] {"val1", "val2"})
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Map<String, String> maMap = new HashMap<>();
    maMap.put(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]);

    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .managedAttributes(maMap)
      .build();
    assertDoesNotThrow(() -> collectingEventService.create(collectingEvent));
  }

  @Test
  void assignedValueNotContainedInAcceptedValues_validationPasses() {
    CollectionControlledVocabularyItem testManagedAttribute =
      CollectionControlledVocabularyItemFactory
        .newCollectionManagedAttribute()
        .acceptedValues(new String[] {"val1", "val2"})
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(testManagedAttribute);

    Map<String, String> maMap = new HashMap<>();
    maMap.put(testManagedAttribute.getKey(), "val3");

    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .managedAttributes(maMap)
      .build();
    assertThrows(ValidationException.class, () ->  collectingEventService.update(collectingEvent));
  }
}
