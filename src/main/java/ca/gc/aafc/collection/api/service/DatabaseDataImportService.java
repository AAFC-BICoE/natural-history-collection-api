package ca.gc.aafc.collection.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.DinaDataImport;
import ca.gc.aafc.dina.jpa.BaseDAO;

/**
* Service for querying data import tracking records.
*
* <p>Provides access to pending imports recorded in the
* {@code dina_data_import} table.</p>
*/
@Service
public class DatabaseDataImportService {

  private final BaseDAO baseDAO;

  public DatabaseDataImportService(BaseDAO baseDAO) {
    this.baseDAO = baseDAO; 
  }


  /**
   * Retrieves all import entries that have not yet been processed.
   *
   * @return list of pending import entries
   */
  public List<DinaDataImport> findPendingImports() {
    String jpql = """
        from DinaDataImport d
        where d.status is null
        """;

    return baseDAO.findAllByQuery(
        DinaDataImport.class,
        jpql, null);
  }
}
