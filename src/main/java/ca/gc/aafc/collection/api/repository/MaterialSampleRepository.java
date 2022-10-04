package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;
import io.micrometer.core.annotation.Timed;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

@Log4j2
@Repository
public class MaterialSampleRepository extends DinaRepository<MaterialSampleDto, MaterialSample> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public MaterialSampleRepository(
      @NonNull MaterialSampleService dinaService,
      ExternalResourceProvider externalResourceProvider,
      @NonNull AuditService auditService,
      Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
      DinaAuthorizationService groupAuthorizationService,
      @NonNull BuildProperties buildProperties,
      ObjectMapper objectMapper
  ) {
    super(
        dinaService,
        groupAuthorizationService,
        Optional.of(auditService),
        new DinaMapper<>(MaterialSampleDto.class),
        MaterialSampleDto.class,
        MaterialSample.class,
        null,
        externalResourceProvider,
        buildProperties, objectMapper);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends MaterialSampleDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
        authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }

  @Timed(value = "dina.repository.material-sample.findAll.time", description = "Time taken to findAll on MaterialSampleRepository")
  @Override
  public ResourceList<MaterialSampleDto> findAll(Collection<Serializable> ids, QuerySpec querySpec) {
    log.debug("QuerySpec included relations: {}", querySpec.getIncludedRelations());
    return super.findAll(ids, querySpec);
  }

  @Timed(value = "dina.repository.material-sample.findOne.time", description = "Time taken to findOne on MaterialSampleRepository")
  @Override
  public MaterialSampleDto findOne(Serializable id, QuerySpec querySpec) {
    log.debug("QuerySpec included relations: {}", querySpec.getIncludedRelations());
    return super.findOne(id, querySpec);
  }

}
