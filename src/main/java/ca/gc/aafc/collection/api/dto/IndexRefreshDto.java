package ca.gc.aafc.collection.api.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@AllArgsConstructor
@Getter
@JsonApiTypeForClass(IndexRefreshDto.TYPE)
public class IndexRefreshDto {

  public static final String TYPE = "index-refresh";

  @JsonApiId
  private final UUID id;

  private final String docType;

}
