package ca.gc.aafc.collection.api.repository;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.dto.ResourceNameIdentifierResponseDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.repository.ResourceNameIdentifierBaseRepository;
import ca.gc.aafc.dina.service.NameUUIDPair;
import ca.gc.aafc.dina.service.ResourceNameIdentifierService;

import static com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder.jsonApiModel;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * Endpoint used to get identifiers (uuid) based on name.
 */
@RestController
@RequestMapping(value = "/api/v1", produces = JSON_API_VALUE)
public class ResourceNameIdentifierRepository extends ResourceNameIdentifierBaseRepository {

  public ResourceNameIdentifierRepository(
    ResourceNameIdentifierService resourceNameIdentifierService) {
    super(resourceNameIdentifierService,
      Map.of(CollectionDto.TYPENAME, Collection.class,
        ProjectDto.TYPENAME, Project.class,
        StorageUnitDto.TYPENAME, StorageUnit.class));
  }

  @GetMapping(ResourceNameIdentifierResponseDto.TYPE)
  public ResponseEntity<RepresentationModel<?>> findAll(HttpServletRequest req) {

    String query = URLDecoder.decode(req.getQueryString(), StandardCharsets.UTF_8);
    List<ResourceNameIdentifierResponseDto> dtos ;
    try {
      List<NameUUIDPair> identifiers = findAll(query);

      dtos = identifiers.stream().map(nuPair -> ResourceNameIdentifierResponseDto.builder()
        .id(nuPair.uuid())
        .name(nuPair.name())
        .build()).toList();

    } catch (IllegalArgumentException iaEx) {
      return ResponseEntity.badRequest().build();
    }

    JsonApiModelBuilder builder = jsonApiModel().model(CollectionModel.of(dtos));

    return ResponseEntity.ok(builder.build());
  }

}
