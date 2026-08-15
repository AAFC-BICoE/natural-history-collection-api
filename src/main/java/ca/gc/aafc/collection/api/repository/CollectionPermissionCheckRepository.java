package ca.gc.aafc.collection.api.repository;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.gc.aafc.dina.dto.PermissionCheckDto;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.repository.PermissionCheckRepository;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class CollectionPermissionCheckRepository extends PermissionCheckRepository {

  public CollectionPermissionCheckRepository(List<DinaRepositoryV2<?, ?>> dinaRepositories) {
    super(dinaRepositories);
  }

  @PostMapping(PermissionCheckDto.TYPE_NAME)
  public ResponseEntity<RepresentationModel<?>> onCreate(@RequestBody JsonApiDocument postedDocument)
      throws ResourceNotFoundException {
    return handleCheckPermissions(postedDocument);
  }
}
