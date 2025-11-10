package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.SplitConfigurationDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.SplitConfigurationTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import javax.inject.Inject;

public class SplitConfigurationRepositoryIT extends BaseRepositoryIT {

  @Inject
  private SplitConfigurationRepository splitConfigurationRepository;

  @Test
  @WithMockKeycloakUser(username = "dev",
    groupRole = {SplitConfigurationTestFixture.GROUP + ":SUPER_USER"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
    throws ResourceGoneException, ResourceNotFoundException {
    SplitConfigurationDto splitConfigurationDto =
      SplitConfigurationTestFixture.newSplitConfiguration();

    UUID splitConfigurationUUID = createWithRepository(splitConfigurationDto, splitConfigurationRepository::onCreate);

    SplitConfigurationDto result = splitConfigurationRepository.getOne(splitConfigurationUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertNotNull(result.getConditionalOnMaterialSampleTypes());
    assertEquals(splitConfigurationDto.getName(), result.getName());
    assertEquals(splitConfigurationDto.getGroup(), result.getGroup());
  }
}
