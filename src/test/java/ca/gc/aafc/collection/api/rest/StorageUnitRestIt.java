package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.transaction.Transactional;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class StorageUnitRestIt extends BaseRestAssuredTest {

  protected StorageUnitRestIt() {
    super("/api/v1");
  }

  @Test
  void post() {
    StorageUnitDto unit = newUnit();
    String unitId = sendPost(
      StorageUnitDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(StorageUnitDto.TYPENAME, unit)
    ).extract().body().jsonPath().getString("data.id");

    sendGet(StorageUnitDto.TYPENAME, unitId)
      .body("data.attributes.name", Matchers.is(unit.getName()))
      .body("data.attributes.group", Matchers.is(unit.getGroup()))
      .body("data.attributes.createdBy", Matchers.notNullValue())
      .body("data.attributes.createdOn", Matchers.notNullValue());
  }

  private StorageUnitDto newUnit() {
    StorageUnitDto unitDto = new StorageUnitDto();
    unitDto.setName(RandomStringUtils.randomAlphabetic(3));
    unitDto.setGroup(RandomStringUtils.randomAlphabetic(4));
    unitDto.setStorageUnitChildren(null);
    unitDto.setParentStorageUnit(null);
    return unitDto;
  }

}
