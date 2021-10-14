package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleTypeDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleType;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleTypeFactory;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleTypeRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleTypeRepository materialSampleTypeRepository;

  private static final String name = "preparation process definition";

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:DINA_ADMIN"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    MaterialSampleTypeDto mst = newMaterialSampleTypeDto();
    MaterialSampleTypeDto result = materialSampleTypeRepository.findOne(
      materialSampleTypeRepository.create(mst).getUuid(),
      new QuerySpec(MaterialSampleTypeDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(mst.getName(), result.getName());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC: staff"})
  public void updateFromDifferentGroup_throwAccessDenied() {
      MaterialSampleType testMaterialSampleType = MaterialSampleTypeFactory.newMaterialSampleType()
        .name(name)
        .build();
      materialSampleTypeService.create(testMaterialSampleType);
      MaterialSampleTypeDto retrievedMaterialSampleType = materialSampleTypeRepository.findOne(testMaterialSampleType.getUuid(),
          new QuerySpec(MaterialSampleTypeDto.class));
      assertThrows(AccessDeniedException.class, () -> materialSampleTypeRepository.save(retrievedMaterialSampleType));
  }

  private MaterialSampleTypeDto newMaterialSampleTypeDto() {
    MaterialSampleTypeDto mst = new MaterialSampleTypeDto();
    mst.setName(name);
    mst.setUuid(UUID.randomUUID());
    return mst;
  }
  
}
