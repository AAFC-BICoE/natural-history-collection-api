package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.service.OrganismService;
import ca.gc.aafc.dina.json.JsonDocumentInspector;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import ca.gc.aafc.dina.security.TextHtmlSanitizer;
import ca.gc.aafc.dina.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class OrganismRepository extends DinaRepository<OrganismDto, Organism> {

  private static final Safelist SIMPLE_TEXT = Safelist.simpleText();

  private final DinaAuthenticatedUser dinaAuthenticatedUser;
  private final ObjectMapper objectMapper;

  public OrganismRepository(
          @NonNull OrganismService dinaService,
          ExternalResourceProvider externalResourceProvider,
          DinaAuthorizationService groupAuthorizationService,
          @NonNull BuildProperties buildProperties,
          AuditService auditService,
          Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
          ObjectMapper objectMapper
  ) {
    super(dinaService, groupAuthorizationService, Optional.of(auditService),
        new DinaMapper<>(OrganismDto.class), OrganismDto.class,
        Organism.class, null, externalResourceProvider, buildProperties, objectMapper);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser.orElse(null);
    this.objectMapper = objectMapper;
  }

  @Override
  public <S extends OrganismDto> S create(S resource) {
    if (dinaAuthenticatedUser != null) {
      resource.setCreatedBy(dinaAuthenticatedUser.getUsername());
    }
    return super.create(resource);
  }

  /**
   * We override the checkMethod to use a less aggressive check since Determination can have simple text html.
   * @param resource
   * @param <S>
   */
  @Override
  protected <S extends OrganismDto> void checkSubmittedData(S resource) {
    Objects.requireNonNull(this.objectMapper);
    if (!JsonDocumentInspector.testPredicateOnValues(objectMapper.convertValue(resource, IT_OM_TYPE_REF),
            OrganismRepository::isSafeSimpleText)) {
      throw new IllegalArgumentException("unsafe value detected in attributes");
    }
  }

  private static boolean isSafeSimpleText(String txt) {
    return StringUtils.isBlank(txt) || Jsoup.isValid(txt, SIMPLE_TEXT);
  }

}
