package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.PreparationProcessService;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

public class PreparationProcessCRUDIT extends CollectionModuleBaseIT {
  @Inject
  private BaseDAO baseDAO;
  private DefaultDinaService<PreparationProcess> maService;

  @BeforeEach
  void setUp() {
    maService = new PreparationProcessService(baseDAO);
  }

  @Test
  void create() {
    PreparationProcess build = PreparationProcess.builder()
      .createdBy("dina")
      .build();
    maService.create(build);
    Assertions.assertNotNull(build.getId());
    Assertions.assertNotNull(build.getCreatedOn());
    Assertions.assertNotNull(build.getUuid());
  }
}
