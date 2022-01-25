package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CustomViewFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomViewCRUDIT extends CollectionModuleBaseIT {

  public static final String EXPECTED_GROUP = "g1";
  public static final String EXPECTED_CREATED_BY = "createdBy";

  @Test
  void createAndFind() {
    CustomView customView = customViewService.createAndFlush(
        CustomViewFactory.newCustomView()
            .restrictToCreatedBy(true)
            .group(EXPECTED_GROUP)
            .createdBy(EXPECTED_CREATED_BY)
        .build());

    // make sure we can create a second CustomView on the same group
    CustomView customView2 = customViewService.createAndFlush(
        CustomViewFactory.newCustomView()
            .group(EXPECTED_GROUP)
            .createdBy(EXPECTED_CREATED_BY)
            .build());

    CustomView result = customViewService.findOne(customView.getUuid(), CustomView.class);
    assertNotNull(result.getCreatedOn());
    assertEquals(customView.getName(), result.getName());
    assertEquals(EXPECTED_GROUP, result.getGroup());
    assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    assertTrue(result.getRestrictToCreatedBy());

    //cleanup
    customViewService.delete(customView);
    customViewService.delete(customView2);
  }

}
