package ca.gc.aafc.collection.api.repository;

import org.springframework.boot.info.BuildProperties;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import ca.gc.aafc.collection.api.dto.MaterialSampleSummaryDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleSummary;
import ca.gc.aafc.collection.api.service.MaterialSampleSummaryService;
import ca.gc.aafc.dina.dto.JsonApiDto;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.exception.ResourcesGoneException;
import ca.gc.aafc.dina.exception.ResourcesNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkResourceIdentifierDocument;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;

import static ca.gc.aafc.dina.repository.DinaRepositoryV2.JSON_API_BULK;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MaterialSampleSummary loads a minimum set of data and returns the effective determination.
 *
 */
@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class MaterialSampleSummaryRepository {

  private final MaterialSampleSummaryService mssService;
  protected final JsonApiModelAssistant<MaterialSampleSummaryDto> jsonApiModelAssistant;

  protected MaterialSampleSummaryRepository(BuildProperties buildProperties,
                                            MaterialSampleSummaryService mssService) {
    this.mssService = mssService;
    this.jsonApiModelAssistant = new JsonApiModelAssistant<>(buildProperties.getVersion());
  }

  public JsonApiDto<MaterialSampleSummaryDto> getOne(UUID uuid) throws ResourceNotFoundException {
    MaterialSampleSummary mss = mssService.findMaterialSampleSummary(uuid);

    if (mss == null) {
      throw ResourceNotFoundException.create(MaterialSampleSummaryDto.TYPENAME, uuid);
    }

    return JsonApiDto.<MaterialSampleSummaryDto>builder().dto(MaterialSampleSummaryDto.builder()
      .uuid(mss.getUuid())
      .materialSampleName(mss.getMaterialSampleName())
      .effectiveDeterminations(mss.getEffectiveDeterminations())
      .build()).build();
  }

  @PostMapping(path = MaterialSampleSummaryDto.TYPENAME + "/" + DinaRepositoryV2.JSON_API_BULK_LOAD_PATH, consumes = JSON_API_BULK)
  @Transactional(readOnly = true)
  public ResponseEntity<RepresentationModel<?>> onBulkLoad(@RequestBody
                                                           JsonApiBulkResourceIdentifierDocument jsonApiBulkDocument)
      throws ResourcesNotFoundException, ResourcesGoneException {

    List<JsonApiDto<MaterialSampleSummaryDto>> dtos = new ArrayList<>();

    // initialize to null since it won't be used most of the time
    List<String> resourcesNotFound = null;

    for (var data : jsonApiBulkDocument.getData()) {
      try {
        dtos.add(getOne(data.getId()));
      } catch (ResourceNotFoundException exNotFound) {
        if (resourcesNotFound == null) {
          resourcesNotFound = new ArrayList<>();
        }
        resourcesNotFound.add(exNotFound.getIdentifier());
      }
    }

    // errors handling
    if (resourcesNotFound != null) {
      throw ResourcesNotFoundException.create(MaterialSampleSummaryDto.TYPENAME, resourcesNotFound);
    }
    JsonApiModelBuilder builder = jsonApiModelAssistant.createJsonApiModelBuilder(dtos, null);

    return ResponseEntity.ok().body(builder.build());
  }

  @GetMapping(MaterialSampleSummaryDto.TYPENAME + "/{id}")
  @Transactional(readOnly = true)
  public ResponseEntity<RepresentationModel<?>> handleFindOne(@PathVariable UUID id)
      throws ResourceNotFoundException {

    JsonApiDto<MaterialSampleSummaryDto> jsonApiDto =getOne(id);
    JsonApiModelBuilder builder = jsonApiModelAssistant.createJsonApiModelBuilder(jsonApiDto);
    return ResponseEntity.ok(builder.build());
  }
}
