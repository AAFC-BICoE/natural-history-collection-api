package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import io.crnk.core.queryspec.QuerySpec;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;

@SpringBootTest(properties = "dev-user.enabled=true")
class StorageUnitTypeRepoIT extends CollectionModuleBaseIT {

  @Inject
  private StorageUnitTypeRepo repo;
  private StorageUnitTypeDto typeDto;

  @BeforeEach
  void setUp() {
    typeDto = new StorageUnitTypeDto();
    typeDto.setName(RandomStringUtils.randomAlphabetic(4));
    typeDto.setGroup(RandomStringUtils.randomAlphabetic(4));
    typeDto = repo.create(typeDto);
  }

  @Test
  void find() {
    StorageUnitTypeDto result = repo.findOne(typeDto.getUuid(), new QuerySpec(StorageUnitTypeDto.class));
    Assertions.assertNotNull(result.getCreatedBy());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(typeDto.getName(), result.getName());
    Assertions.assertEquals(typeDto.getGroup(), result.getGroup());
  }
}