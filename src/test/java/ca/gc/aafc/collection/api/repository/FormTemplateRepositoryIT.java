package ca.gc.aafc.collection.api.repository;

import java.util.UUID;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.FormTemplateDto;
import ca.gc.aafc.collection.api.entities.FormTemplate;
import ca.gc.aafc.collection.api.service.FormTemplateService;
import ca.gc.aafc.collection.api.testsupport.factories.FormTemplateFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.FormTemplateFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import io.crnk.core.queryspec.QuerySpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled=true")
public class FormTemplateRepositoryIT extends CollectionModuleBaseIT {
  
  @Inject 
  private FormTemplateRepository formTemplateRepository;

  @Inject
  private FormTemplateService formTemplateService;

  private static final String NAME = "My Form Template";

  @Test
  @WithMockKeycloakUser(groupRole = FormTemplateFixture.GROUP + ":user")
  public void create_OnCreate_SetsCreatedBy() {

    FormTemplateDto cvDto = FormTemplateFixture.newFormTemplate().name(NAME).build();

    UUID createResourceUUID = formTemplateRepository.create(cvDto).getUuid();

    FormTemplateDto result = formTemplateRepository.findOne(
        createResourceUUID, new QuerySpec(FormTemplateDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(NAME, result.getName());
    assertEquals(FormTemplateFixture.GROUP, result.getGroup());

    formTemplateRepository.delete(createResourceUUID);
  }

  @Test
  @WithMockKeycloakUser(groupRole = FormTemplateFixture.GROUP + ":user")
  public void update_OnNameUpdate_NameUpdated() {

    FormTemplateDto cvDto = FormTemplateFixture.newFormTemplate().name(NAME).build();
    UUID createResourceUUID = formTemplateRepository.create(cvDto).getUuid();

    FormTemplateDto result = formTemplateRepository.findOne(
        createResourceUUID, new QuerySpec(FormTemplateDto.class));

    result.setName("new name");
    formTemplateRepository.save(result);

    result = formTemplateRepository.findOne(
        createResourceUUID, new QuerySpec(FormTemplateDto.class));

    assertEquals("new name", result.getName());

    formTemplateRepository.delete(createResourceUUID);
  }

  @Test
  @WithMockKeycloakUser(groupRole = FormTemplateFixture.GROUP + ":user")
  public void update_NotOwner_updateRejected() {
    // service won't apply authorization
    FormTemplate ft = FormTemplateFactory.newFormTemplate().createdBy("xyz").build();
    UUID createResourceUUID = formTemplateService.create(ft).getUuid();

    FormTemplateDto cvDto = formTemplateRepository.findOne(
      createResourceUUID, new QuerySpec(FormTemplateDto.class));

    cvDto.setName("new name");
    assertThrows(AccessDeniedException.class, () -> formTemplateRepository.save(cvDto));
  }

}
