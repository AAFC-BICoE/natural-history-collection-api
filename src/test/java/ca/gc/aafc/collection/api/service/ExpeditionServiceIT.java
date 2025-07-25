package ca.gc.aafc.collection.api.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.collection.api.testsupport.factories.ExpeditionFactory;
import ca.gc.aafc.dina.entity.AgentRoles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpeditionServiceIT extends CollectionModuleBaseIT {

  @Test
  void expedition_OnCreate_expeditionCreated() {
    Expedition expedition = ExpeditionFactory.newExpedition()
      .build();

    expeditionService.create(expedition);

    Expedition expeditionReloaded = expeditionService.findOne(expedition.getUuid(), Expedition.class);

    assertEquals(expedition.getName(), expeditionReloaded.getName());
  }
}
