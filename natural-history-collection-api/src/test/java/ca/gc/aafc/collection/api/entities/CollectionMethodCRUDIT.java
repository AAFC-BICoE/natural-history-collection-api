package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.CollectionMethodService;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import java.util.List;

class CollectionMethodCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionMethodService methodService;

  @Test
  void find() {
    CollectionMethod expected = methodService.create(newMethod());
    CollectionMethod result = methodService.findOne(expected.getUuid(), CollectionMethod.class);
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(expected.getName(), result.getName());
    Assertions.assertEquals(expected.getGroup(), result.getGroup());
    Assertions.assertEquals(expected.getCreatedBy(), result.getCreatedBy());
    assertDescription(expected.getMultilingualDescription(), result.getMultilingualDescription());
  }

  private void assertDescription(
    MultilingualDescription expected,
    MultilingualDescription result
  ) {
    if (expected == null) {
      Assertions.assertNull(result);
    } else {
      List<MultilingualDescription.MultilingualPair> expectedDescriptions = expected.getDescriptions();
      List<MultilingualDescription.MultilingualPair> resultDescriptions = result.getDescriptions();
      if (CollectionUtils.isEmpty(expectedDescriptions)) {
        Assertions.assertTrue(CollectionUtils.isEmpty(resultDescriptions), "Descriptions should be empty");
      } else {
        Assertions.assertEquals(expectedDescriptions.size(), resultDescriptions.size());
        expectedDescriptions.forEach(
          multilingualPair -> assertContainsPair(resultDescriptions, multilingualPair));
      }
    }
  }

  private void assertContainsPair(
    List<MultilingualDescription.MultilingualPair> resultDescriptions,
    MultilingualDescription.MultilingualPair multilingualPair
  ) {
    MultilingualDescription.MultilingualPair parsedPair = resultDescriptions.stream()
      .filter(pair -> pair.getDesc().equals(multilingualPair.getDesc()) && pair.getLang()
        .equals(multilingualPair.getLang()))
      .findFirst().orElse(null);
    if (parsedPair == null) {
      Assertions.fail("Pair was not present in the descriptions");
    }
  }

  private CollectionMethod newMethod() {
    return CollectionMethod.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .createdBy(RandomStringUtils.randomAlphabetic(4))
      .group(RandomStringUtils.randomAlphabetic(4))
      .multilingualDescription(newMulti())
      .build();
  }

  private static MultilingualDescription newMulti() {
    return MultilingualDescription.builder()
      .descriptions(List.of(MultilingualDescription.MultilingualPair.builder()
        .desc(RandomStringUtils.randomAlphabetic(4))
        .lang("en")
        .build()))
      .build();
  }
}
