package ca.gc.aafc.collection.api.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.gc.aafc.collection.api.dto.IndexRefreshDto;
import ca.gc.aafc.collection.api.service.IndexRefreshService;
import ca.gc.aafc.dina.security.auth.DinaAdminCUDAuthorizationService;
import ca.gc.aafc.dina.security.auth.DinaAuthorizationService;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

/**
 * Administrative endpoint to refresh a document in the search index.
 *
 */
@RestController
@RequestMapping(value = "/api/v1", produces = JSON_API_VALUE)
@ConditionalOnProperty(prefix = "dina.messaging", name = "isProducer", havingValue = "true")
public class IndexRefreshRepository {

  public final DinaAuthorizationService dinaAdminCUDAuthorizationService;
  public final IndexRefreshService indexRefreshService;

  public IndexRefreshRepository(DinaAdminCUDAuthorizationService dinaAdminCUDAuthorizationService,
                                IndexRefreshService indexRefreshService) {
    this.dinaAdminCUDAuthorizationService = dinaAdminCUDAuthorizationService;
    this.indexRefreshService = indexRefreshService;
  }

  @PostMapping("/" + IndexRefreshDto.TYPE)
  public ResponseEntity<?> handlePost(@RequestBody EntityModel<IndexRefreshDto> indexRefresh) {
    IndexRefreshDto indexRefreshDto = indexRefresh.getContent();

    dinaAdminCUDAuthorizationService.authorizeCreate(indexRefreshDto);

    if (indexRefreshDto == null) {
      return ResponseEntity.badRequest().build();
    }

    try {
      indexRefreshService.reindexDocument(indexRefreshDto.getId().toString(),
        indexRefreshDto.getDocType());
    } catch (IllegalStateException isEx) {
      return ResponseEntity.badRequest().build();
    }
    // since this is async we return accepted instead of created
    return ResponseEntity.accepted().build();
  }
}

