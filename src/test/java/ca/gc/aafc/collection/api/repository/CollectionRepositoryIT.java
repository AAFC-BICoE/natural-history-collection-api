package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionRepository collectionRepository;

  private static final String group = "aafc";
  private static final String name = "preparation process definition";
  private static final String code = "YUL";

  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    CollectionDto collectionDto = newCollectionDto();
    CollectionDto result = collectionRepository.findOne(
      collectionRepository.create(collectionDto).getUuid(),
      new QuerySpec(CollectionDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(collectionDto.getName(), result.getName());
    assertEquals(collectionDto.getGroup(), result.getGroup());
    assertEquals(collectionDto.getCode(), result.getCode());
  }

  private CollectionDto newCollectionDto() {
    CollectionDto collectionDto = new CollectionDto();
    collectionDto.setName(name);
    collectionDto.setGroup(group);
    collectionDto.setCode(code);
    return collectionDto;
   }
  
}
