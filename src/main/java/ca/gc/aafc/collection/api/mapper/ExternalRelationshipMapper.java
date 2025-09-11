package ca.gc.aafc.collection.api.mapper;


import java.util.UUID;

import ca.gc.aafc.collection.api.dto.external.MetadataExternalDto;
import ca.gc.aafc.collection.api.dto.external.PersonExternalDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.JsonApiExternalResource;

/**
 * Not a MapStruct mapper
 *
 * Map known external Relationship types
 */
public final class ExternalRelationshipMapper {

  private ExternalRelationshipMapper() {
    //utility class
  }

  public static JsonApiExternalResource externalRelationDtoToJsonApiExternalResource(ExternalRelationDto externalRelationDto) {
    if (externalRelationDto == null) {
      return null;
    }

    return switch (externalRelationDto.getType()) {
      case PersonExternalDto.EXTERNAL_TYPENAME -> PersonExternalDto.builder().uuid(UUID.fromString(externalRelationDto.getId())).build();
      case MetadataExternalDto.EXTERNAL_TYPENAME -> MetadataExternalDto.builder().uuid(UUID.fromString(externalRelationDto.getId())).build();
      default -> null;
    };
  }

}
