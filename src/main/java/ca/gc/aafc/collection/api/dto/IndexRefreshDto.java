package ca.gc.aafc.collection.api.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonApiTypeForClass(IndexRefreshDto.TYPE)
public class IndexRefreshDto {

  public static final String TYPE = "index-refresh";

  @JsonApiId
  private UUID id;

  private String docType;

}
