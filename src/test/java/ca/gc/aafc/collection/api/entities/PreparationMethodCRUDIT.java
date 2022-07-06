package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MultilingualDescriptionFactory;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationMethodFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreparationMethodCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "my preparation method";
  private static final String EXPECTED_GROUP = "DINA GROUP";
  private static final String EXPECTED_CREATED_BY = "createdBy";

  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION =
          MultilingualDescriptionFactory.newMultilingualDescription();

  private PreparationMethod buildTestPreparationMethod() {
    return PreparationMethodFactory.newPreparationMethod()
            .name(EXPECTED_NAME)
            .group(EXPECTED_GROUP)
            .createdBy(EXPECTED_CREATED_BY)
            .multilingualDescription(MULTILINGUAL_DESCRIPTION)
            .build();
  }

  @Test
  void create() {
    PreparationMethod prepMethod = buildTestPreparationMethod();
    preparationMethodService.create(prepMethod);

    assertNotNull(prepMethod.getId());
    assertNotNull(prepMethod.getCreatedOn());
    assertNotNull(prepMethod.getUuid());
  }

  @Test
  void createDuplicates_ThrowsException() {
    PreparationMethod prepMethod = buildTestPreparationMethod();
    preparationMethodService.createAndFlush(prepMethod);

    PreparationMethod prepMethod2 = buildTestPreparationMethod();
    //change the group so create should work
    prepMethod2.setGroup("grp2");
    preparationMethodService.createAndFlush(prepMethod2);

    //set the same group and try to update
    prepMethod2.setGroup(EXPECTED_GROUP);
    assertThrows(PersistenceException.class, () -> preparationMethodService.update(prepMethod2));
  }

  @Test
  void find() {
    PreparationMethod prepMethod = buildTestPreparationMethod();
    preparationMethodService.create(prepMethod);

    PreparationMethod result = preparationMethodService.findOne(
            prepMethod.getUuid(),
            PreparationMethod.class);

    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(), result.getMultilingualDescription().getDescriptions());
  }
}
