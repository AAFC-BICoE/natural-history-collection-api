package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.FormTemplateDto;
import ca.gc.aafc.collection.api.entities.FormTemplate;
import ca.gc.aafc.collection.api.service.FormTemplateService;
import ca.gc.aafc.collection.api.testsupport.factories.FormTemplateFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.FormTemplateFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.TransactionTestingHelper;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import javax.inject.Inject;

public class FormTemplateRepositoryIT extends BaseRepositoryIT {
  
  @Inject 
  private FormTemplateRepository formTemplateRepository;

  @Inject
  private FormTemplateService formTemplateService;

  @Inject
  private TransactionTestingHelper transactionTestingHelper;

  private static final String NAME = "My Form Template";

  @Test
  @WithMockKeycloakUser(groupRole = FormTemplateFixture.GROUP + ":user")
  public void create_OnCreate_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {

    FormTemplateDto dto = FormTemplateFixture.newFormTemplate().name(NAME).build();
    UUID createResourceUUID = createWithRepository(dto, formTemplateRepository::onCreate);

    FormTemplateDto result = formTemplateRepository.getOne(createResourceUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(NAME, result.getName());
    assertEquals(FormTemplateFixture.GROUP, result.getGroup());

    formTemplateRepository.onDelete(createResourceUUID);
  }

  @Test
  @WithMockKeycloakUser(groupRole = FormTemplateFixture.GROUP + ":user")
  public void update_OnNameUpdate_NameUpdated()
      throws ResourceGoneException, ResourceNotFoundException {

    FormTemplateDto dto = FormTemplateFixture.newFormTemplate().name(NAME).build();
    UUID createResourceUUID = createWithRepository(dto, formTemplateRepository::onCreate);

    FormTemplateDto result = formTemplateRepository.getOne(createResourceUUID, "").getDto();

    result.setName("new name");
    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      createResourceUUID, dto.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(result)
    );
    formTemplateRepository.onUpdate(docToUpdate, createResourceUUID);

    result = formTemplateRepository.getOne(createResourceUUID, "").getDto();

    assertEquals("new name", result.getName());
    formTemplateRepository.onDelete(createResourceUUID);
  }

  @Test
  @WithMockKeycloakUser(groupRole = FormTemplateFixture.GROUP + ":user")
  public void update_NotOwner_updateRejected()
      throws ResourceGoneException, ResourceNotFoundException {
    // service won't apply authorization
    FormTemplate ft = FormTemplateFactory.newFormTemplate().createdBy("xyz").build();
    UUID createResourceUUID =
      transactionTestingHelper.doInTransaction(() -> formTemplateService.create(ft).getUuid());

    FormTemplateDto dto = formTemplateRepository.getOne(createResourceUUID, "").getDto();

    dto.setName("new name");
    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      createResourceUUID, dto.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(dto)
    );

    assertThrows(AccessDeniedException.class, () -> formTemplateRepository.onUpdate(docToUpdate, createResourceUUID));
  }
}
