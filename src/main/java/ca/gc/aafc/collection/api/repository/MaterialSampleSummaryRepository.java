package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleSummaryDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleSummary;
import ca.gc.aafc.collection.api.service.MaterialSampleSummaryService;
import static com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder.jsonApiModel;
import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;

import java.util.UUID;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

/**
 * MaterialSampleSummary loads a minimum set of data and returns the effective determination.
 *
 */
@RestController
@RequestMapping(value = "${dina.apiPrefix:}", produces = JSON_API_VALUE)
public class MaterialSampleSummaryRepository {

  private final MaterialSampleSummaryService mssService;

  protected MaterialSampleSummaryRepository(MaterialSampleSummaryService mssService) {
    this.mssService = mssService;
  }

  @Transactional(
    readOnly = true
  )
  
  public MaterialSampleSummaryDto findOne(UUID uuid) {
    MaterialSampleSummary mss = mssService.findMaterialSampleSummary(uuid);

    if (mss == null) {
      return null;
    }

    return MaterialSampleSummaryDto.builder()
      .uuid(mss.getUuid())
      .materialSampleName(mss.getMaterialSampleName())
      .effectiveDeterminations(mss.getEffectiveDeterminations())
      .build();
  }

  @GetMapping(MaterialSampleSummaryDto.TYPENAME + "/{id}")
  public ResponseEntity<RepresentationModel<?>> handleFindOne(@PathVariable String id) {

    MaterialSampleSummaryDto dto = findOne(UUID.fromString(id));

    if (dto == null) {
      return ResponseEntity.notFound().build();
    }

    JsonApiModelBuilder builder = jsonApiModel().model(RepresentationModel.of(dto));

    return ResponseEntity.ok(builder.build());
  }
}
