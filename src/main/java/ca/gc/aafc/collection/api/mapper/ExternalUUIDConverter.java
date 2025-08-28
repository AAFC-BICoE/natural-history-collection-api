package ca.gc.aafc.collection.api.mapper;

import java.util.List;
import java.util.UUID;
import org.mapstruct.Named;

import ca.gc.aafc.dina.dto.ExternalRelationDto;

/**
 * To be replaced but dina-base version (0.150)
 */
public class ExternalUUIDConverter {

  @Named("uuidToMetadataExternalRelations")
  public static List<ExternalRelationDto> uuidToMetadataExternalRelations(List<UUID> metadataUUIDs) {
    return metadataUUIDs == null ? null :
      metadataUUIDs.stream().map(uuid ->
        ExternalRelationDto.builder().id(uuid.toString()).type("metadata").build()).toList();
  }

  @Named("uuidToPersonExternalRelations")
  public static List<ExternalRelationDto> uuidToPersonExternalRelations(List<UUID> personUUID) {
    return personUUID == null ? null :
      personUUID.stream().map(uuid ->
        ExternalRelationDto.builder().id(uuid.toString()).type("person").build()).toList();
  }


}
