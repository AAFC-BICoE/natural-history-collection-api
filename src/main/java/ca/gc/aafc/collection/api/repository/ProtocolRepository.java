package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import ca.gc.aafc.collection.api.security.SuperUserInGroupCUDAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.collection.api.service.ProtocolService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;


@Repository
public class ProtocolRepository extends DinaRepository<ProtocolDto, Protocol> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;  

  public ProtocolRepository(
    @NonNull ProtocolService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull AuditService auditService,
    SuperUserInGroupCUDAuthorizationService authorizationService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
    ObjectMapper objectMapper
  ) {
    super(
      dinaService,
      authorizationService,
      Optional.of(auditService),
      new DinaMapper<>(ProtocolDto.class),
      ProtocolDto.class,
      Protocol.class,
      null,
      externalResourceProvider,
      buildProperties, objectMapper);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends ProtocolDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
  
}
