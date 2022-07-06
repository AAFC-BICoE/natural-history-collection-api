package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.FormTemplateFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormTemplateCRUDIT extends CollectionModuleBaseIT {

  public static final String EXPECTED_GROUP = "g1";
  public static final String EXPECTED_CREATED_BY = "createdBy";

  @Test
  void createAndFind() {
    FormTemplate formTemplate = formTemplateService.createAndFlush(
        FormTemplateFactory.newFormTemplate()
            .restrictToCreatedBy(true)
            .group(EXPECTED_GROUP)
            .createdBy(EXPECTED_CREATED_BY)
        .build());

    // make sure we can create a second form template on the same group
    FormTemplate formTemplate2 = formTemplateService.createAndFlush(
        FormTemplateFactory.newFormTemplate()
            .group(EXPECTED_GROUP)
            .createdBy(EXPECTED_CREATED_BY)
            .build());

    FormTemplate result = formTemplateService.findOne(formTemplate.getUuid(), FormTemplate.class);
    assertNotNull(result.getCreatedOn());
    assertEquals(formTemplate.getName(), result.getName());
    assertEquals(EXPECTED_GROUP, result.getGroup());
    assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    assertTrue(result.getRestrictToCreatedBy());

    //cleanup
    formTemplateService.delete(formTemplate);
    formTemplateService.delete(formTemplate2);
  }

}
