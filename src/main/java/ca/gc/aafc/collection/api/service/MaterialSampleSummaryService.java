package ca.gc.aafc.collection.api.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.MaterialSampleSummary;
import ca.gc.aafc.dina.jpa.BaseDAO;

@Service
public class MaterialSampleSummaryService {

  private final BaseDAO baseDAO;

  public MaterialSampleSummaryService(BaseDAO baseDAO) {
    this.baseDAO = baseDAO;
  }

  public MaterialSampleSummary findMaterialSampleSummary(UUID uuid) {
    return baseDAO.findOneByQuery(MaterialSampleSummary.class,
      "FROM MaterialSampleSummary WHERE uuid=:uuid",
      List.of(Pair.of("uuid", uuid)));
  }
}
