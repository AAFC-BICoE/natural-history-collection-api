package ca.gc.aafc.collection.api.repository;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toedter.spring.hateoas.jsonapi.JsonApiError;
import com.toedter.spring.hateoas.jsonapi.JsonApiErrors;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.dto.ResourceNameIdentifierResponseDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.repository.ResourceNameIdentifierBaseRepository;
import ca.gc.aafc.dina.security.auth.GroupWithReadAuthorizationService;
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
    ResourceNameIdentifierService resourceNameIdentifierService,
    GroupWithReadAuthorizationService authorizationService) {
    super(resourceNameIdentifierService,
      authorizationService,
      Map.of(CollectionDto.TYPENAME, Collection.class,
        ProjectDto.TYPENAME, Project.class,
        StorageUnitDto.TYPENAME, StorageUnit.class,
        MaterialSampleDto.TYPENAME, MaterialSample.class,
        PreparationTypeDto.TYPENAME, PreparationType.class,
        PreparationMethodDto.TYPENAME, PreparationMethod.class,
        ProtocolDto.TYPENAME, Protocol.class,
        AssemblageDto.TYPENAME, Assemblage.class));
  }

  @GetMapping(ResourceNameIdentifierResponseDto.TYPE)
  public ResponseEntity<?> findAll(HttpServletRequest req) {

    String query = URLDecoder.decode(req.getQueryString(), StandardCharsets.UTF_8);
    List<ResourceNameIdentifierResponseDto> dtos ;
    try {
      List<NameUUIDPair> identifiers = findAll(query);

      dtos = identifiers.stream().map(nuPair -> ResourceNameIdentifierResponseDto.builder()
        .id(nuPair.uuid())
        .name(nuPair.name())
        .build()).toList();

    } catch (IllegalArgumentException iaEx) {
      return ResponseEntity.badRequest().body(
        JsonApiErrors.create().withError(
          JsonApiError.create()
            .withTitle(HttpStatus.BAD_REQUEST.toString())
            .withStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()))
            .withDetail(iaEx.getMessage())));
    }

    JsonApiModelBuilder builder = jsonApiModel().model(CollectionModel.of(dtos));

    return ResponseEntity.ok(builder.build());
  }

}
