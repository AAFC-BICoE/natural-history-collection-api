package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionMethodDto;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import java.util.List;

@SpringBootTest(
  properties = "keycloak.enabled = true"
)
class CollectionMethodRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionMethodRepository repository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc: staff"})
  void create_WithAuthUser_CreatedBySet() {
    CollectionMethodDto expected = repository.create(newMethod());
    CollectionMethodDto result = repository.findOne(
      expected.getUuid(), new QuerySpec(CollectionMethodDto.class));
    Assertions.assertNotNull(result.getCreatedBy());
    Assertions.assertEquals(expected.getName(), result.getName());
    Assertions.assertEquals(expected.getGroup(), result.getGroup());
    Assertions.assertEquals(expected.getCreatedBy(), result.getCreatedBy());
    Assertions.assertEquals(
      expected.getMultilingualDescription().getDescriptions().get(0).getLang(),
      result.getMultilingualDescription().getDescriptions().get(0).getLang());
  }

  private CollectionMethodDto newMethod() {
    return CollectionMethodDto.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .createdBy(RandomStringUtils.randomAlphabetic(4))
      .group("aafc")
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
