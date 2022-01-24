package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CustomViewDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CustomViewFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CustomViewRepositoryIT extends CollectionModuleBaseIT {
  
  @Inject 
  private CustomViewRepository customViewRepository;

  private static final String NAME = "My Custom View";

  @Test
  @WithMockKeycloakUser(groupRole = CustomViewFixture.GROUP+":user")
  public void create_WithAuthenticatedUser_SetsCreatedBy() {

    CustomViewDto cvDto = CustomViewFixture.newCustomView().name(NAME).build();

    UUID createResourceUUID = customViewRepository.create(cvDto).getUuid();

    CustomViewDto result = customViewRepository.findOne(
        createResourceUUID, new QuerySpec(MaterialSampleActionDefinitionDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(NAME, result.getName());
    assertEquals(CustomViewFixture.GROUP, result.getGroup());

    customViewRepository.delete(createResourceUUID);
  }

}
