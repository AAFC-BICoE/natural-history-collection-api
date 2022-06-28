package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.dina.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionMethodDto;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionMethodTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.IncludeRelationSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectingEventRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private CollectingEventRepository collectingEventRepository;
  @Inject
  CollectionMethodRepository collectionMethodRepository;

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc: staff"})
  public void findCollectingEvent_whenNoFieldsAreSelected_CollectingEventReturnedWithAllFields() {
    CollectingEventDto testCollectingEvent = collectingEventRepository.create(CollectingEventTestFixture.newEventDto());

    QuerySpec querySpec = new QuerySpec(CollectingEventDto.class);

    List<IncludeRelationSpec> includeRelationSpec = Stream.of("geoReferenceAssertions")
      .map(Arrays::asList)
      .map(IncludeRelationSpec::new)
      .collect(Collectors.toList());

    querySpec.setIncludedRelations(includeRelationSpec);

    CollectingEventDto collectingEventDto = collectingEventRepository
      .findOne(testCollectingEvent.getUuid(), querySpec);

    assertNotNull(collectingEventDto);
    assertEquals(testCollectingEvent.getUuid(), collectingEventDto.getUuid());
    assertEquals(testCollectingEvent.getCreatedBy(), collectingEventDto.getCreatedBy());
    assertEquals(
      testCollectingEvent.getStartEventDateTime(),
      collectingEventDto.getStartEventDateTime());
    assertEquals(
      testCollectingEvent.getEndEventDateTime(),
      collectingEventDto.getEndEventDateTime());
    assertEquals(CollectingEventTestFixture.XI_02_1798, collectingEventDto.getVerbatimEventDateTime());

    assertEquals(
      12.123456,
      collectingEventDto.getGeoReferenceAssertions().iterator().next().getDwcDecimalLatitude());

    assertEquals(
      CollectingEventTestFixture.TEST_GEOREFERENCE_DATE,
      collectingEventDto.getGeoReferenceAssertions().iterator().next().getDwcGeoreferencedDate());

    assertEquals(CollectingEventTestFixture.VER_COOR, collectingEventDto.getDwcVerbatimCoordinates());
    assertEquals(CollectingEventTestFixture.DWC_RECORDED_BY, collectingEventDto.getDwcRecordedBy());
    assertEquals(
      testCollectingEvent.getAttachment().get(0).getId(),
      collectingEventDto.getAttachment().get(0).getId());
    assertEquals(
      testCollectingEvent.getCollectors().get(0).getId(),
      collectingEventDto.getCollectors().get(0).getId());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_LOCALITY, collectingEventDto.getDwcVerbatimLocality());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_LATITUDE, collectingEventDto.getDwcVerbatimLatitude());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_LONGITUDE, collectingEventDto.getDwcVerbatimLongitude());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_COORDINATE_SYSTEM, collectingEventDto.getDwcVerbatimCoordinateSystem());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_SRS, collectingEventDto.getDwcVerbatimSRS());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_ELEVATION, collectingEventDto.getDwcVerbatimElevation());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_DEPTH, collectingEventDto.getDwcVerbatimDepth());
    assertEquals(CollectingEventTestFixture.OTHER_RECORD_NUMBERS[1], collectingEventDto.getOtherRecordNumbers()[1]);
    assertEquals(CollectingEventTestFixture.GEOGRAPHIC_PLACE_NAME_SOURCE, collectingEventDto.getGeographicPlaceNameSource());
    assertEquals(CollectingEventTestFixture.HOST, collectingEventDto.getHost());
    assertEquals(
      CollectingEventTestFixture.TEST_COUNTRY,
      collectingEventDto.getGeographicPlaceNameSourceDetail().getCountry());
    // assigned server-side
    assertNotNull(collectingEventDto.getGeographicPlaceNameSourceDetail().getRecordedOn());
    assertNotEquals(2000, collectingEventDto.getGeographicPlaceNameSourceDetail().getRecordedOn().getYear());
    assertNotNull(collectingEventDto.getGeographicPlaceNameSourceDetail().getSourceUrl());
    assertEquals(
      CollectingEventTestFixture.TEST_PROVINCE,
      collectingEventDto.getGeographicPlaceNameSourceDetail().getStateProvince());
    assertEquals(CollectingEventTestFixture.HABITAT, collectingEventDto.getHabitat());
  }

  @WithMockKeycloakUser(groupRole = {"aafc: staff"})
  @Test
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    CollectionMethodDto methodDto = collectionMethodRepository.create(CollectionMethodTestFixture.newMethod());
    CollectingEventDto ce = CollectingEventTestFixture.newEventDto();
    ce.setCollectionMethod(methodDto);
    ce.setStartEventDateTime(ISODateTime.parse("2007-12-03T10:15:30").toString());
    ce.setEndEventDateTime(ISODateTime.parse("2007-12-04T11:20:20").toString());
    QuerySpec querySpec = new QuerySpec(CollectingEventDto.class);
    querySpec.includeRelation(PathSpec.of("collectionMethod"));
    CollectingEventDto result = collectingEventRepository.findOne(
      collectingEventRepository.create(ce).getUuid(),
      querySpec);
    assertNotNull(result.getCreatedBy());
    assertEquals(ce.getAttachment().get(0).getId(), result.getAttachment().get(0).getId());
    assertEquals(ce.getCollectors().get(0).getId(), result.getCollectors().get(0).getId());
    assertEquals(CollectingEventTestFixture.DWC_RECORDED_BY, result.getDwcRecordedBy());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_LOCALITY, result.getDwcVerbatimLocality());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_LATITUDE, result.getDwcVerbatimLatitude());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_LONGITUDE, result.getDwcVerbatimLongitude());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_COORDINATE_SYSTEM, result.getDwcVerbatimCoordinateSystem());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_SRS, result.getDwcVerbatimSRS());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_ELEVATION, result.getDwcVerbatimElevation());
    assertEquals(CollectingEventTestFixture.DWC_VERBATIM_DEPTH, result.getDwcVerbatimDepth());
    assertEquals(CollectingEventTestFixture.OTHER_RECORD_NUMBERS[1], result.getOtherRecordNumbers()[1]);
    assertEquals(CollectingEventTestFixture.HABITAT, result.getHabitat());
    assertEquals(CollectingEventTestFixture.HOST, result.getHost());
    assertAssertion(result.getGeoReferenceAssertions().get(0), ce.getGeoReferenceAssertions().get(0));
    assertEquals(methodDto.getUuid(), result.getCollectionMethod().getUuid());
    MatcherAssert.assertThat(CollectingEventTestFixture.SUBSTRATE, Matchers.is(result.getSubstrate()));
  }

  @WithMockKeycloakUser(groupRole = {"aafc:staff"})
  @Test
  public void create_withUserProvidedUUID_resourceCreatedWithProvidedUUID() {
    UUID myUUID = UUID.randomUUID();
    CollectingEventDto ce = CollectingEventTestFixture.newEventDto();
    ce.setUuid(myUUID);
    collectingEventRepository.create(ce);

    QuerySpec querySpec = new QuerySpec(CollectingEventDto.class);
    CollectingEventDto refreshedCe = collectingEventRepository.findOne(myUUID, querySpec);
    assertNotNull(refreshedCe);
  }

  private void assertAssertion(
    GeoreferenceAssertionDto resultAssertion,
    GeoreferenceAssertionDto expectedAssertion
  ) {
    if (expectedAssertion == null) {
      assertNull(resultAssertion);
      return;
    }
    assertNotNull(resultAssertion);
    assertEquals(expectedAssertion.getDwcDecimalLongitude(), resultAssertion.getDwcDecimalLongitude());
    assertEquals(expectedAssertion.getDwcDecimalLatitude(), resultAssertion.getDwcDecimalLatitude());
    assertEquals(expectedAssertion.getIsPrimary(), resultAssertion.getIsPrimary());
    assertEquals(expectedAssertion.getCreatedOn(), resultAssertion.getCreatedOn());
    assertEquals(expectedAssertion.getDwcGeoreferencedDate(), resultAssertion.getDwcGeoreferencedDate());
    assertEquals(expectedAssertion.getDwcCoordinateUncertaintyInMeters(), resultAssertion.getDwcCoordinateUncertaintyInMeters());
    assertEquals(expectedAssertion.getDwcGeodeticDatum(), resultAssertion.getDwcGeodeticDatum());
    assertEquals(expectedAssertion.getDwcGeoreferenceProtocol(), resultAssertion.getDwcGeoreferenceProtocol());
    assertEquals(expectedAssertion.getDwcGeoreferenceRemarks(), resultAssertion.getDwcGeoreferenceRemarks());
    assertEquals(expectedAssertion.getDwcGeoreferenceSources(), resultAssertion.getDwcGeoreferenceSources());
    assertEquals(expectedAssertion.getDwcGeoreferenceVerificationStatus(), resultAssertion.getDwcGeoreferenceVerificationStatus());
    assertEquals(expectedAssertion.getLiteralGeoreferencedBy(), resultAssertion.getLiteralGeoreferencedBy());
    assertEquals(expectedAssertion.getGeoreferencedBy(), resultAssertion.getGeoreferencedBy());
  }

  @ParameterizedTest
  @MethodSource({"equalFilterSource", "lt_FilterSource", "gt_FilterSource"})
  @WithMockKeycloakUser(groupRole = {"aafc: staff"})
  void findAll_PrecisionBoundsTest_DateFilteredCorrectly(String startDate, String input, int expectedSize) {
    CollectingEventDto ce = CollectingEventTestFixture.newEventDto();
    ce.setStartEventDateTime(ISODateTime.parse(startDate).toString());
    ce.setEndEventDateTime(ISODateTime.parse("2020").toString());
    collectingEventRepository.create(ce);
    assertEquals(expectedSize, collectingEventRepository.findAll(newRsqlQuerySpec(input)).size());
  }

  private static Stream<Arguments> equalFilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("1999", "startEventDateTime==1999", 1),
      Arguments.of("1999", "startEventDateTime==1998", 0),
      // Format YYYY-MM
      Arguments.of("1999-03", "startEventDateTime==1999-03", 1),
      Arguments.of("1999-03", "startEventDateTime==1999-02", 0),
      // Format YYYY-MM-DD
      Arguments.of("1999-03-03", "startEventDateTime==1999-03-03", 1),
      Arguments.of("1999-03-03", "startEventDateTime==1999-03-02", 0),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("1999-03-03T03:00", "startEventDateTime==1999-03-03T03:00", 1),
      Arguments.of("1999-03-03T03:00", "startEventDateTime==1999-03-03T02:00", 0),
      // Format YYYY-MM-DD-HH-MM-SS
      Arguments.of("1999-03-03T03:00:03", "startEventDateTime==1999-03-03T03:00:03", 1),
      Arguments.of("1999-03-03T03:00:03", "startEventDateTime==1999-03-03T03:00:02", 0)
    );
  }

  private static Stream<Arguments> lt_FilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("1999", "startEventDateTime=le=1999", 1),
      Arguments.of("1999", "startEventDateTime=le=1998", 0),

      Arguments.of("1999", "startEventDateTime=lt=1999", 0),
      Arguments.of("1999", "startEventDateTime=lt=2000", 1),

      // Format YYYY-MM
      Arguments.of("1999", "startEventDateTime=le=1999-01", 0),
      Arguments.of("1999-01", "startEventDateTime=le=1999-01", 1),
      Arguments.of("1999-01", "startEventDateTime=le=1998-12", 0),

      Arguments.of("1999-01", "startEventDateTime=lt=1999-01", 0),
      Arguments.of("1999-01", "startEventDateTime=lt=1999-02", 1),

      // Format YYYY-MM-DD
      Arguments.of("1999-01", "startEventDateTime=le=1999-01-01", 0),
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-02", 1),
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-01", 0),

      Arguments.of("1999-01-02", "startEventDateTime=lt=1999-01-02", 0),
      Arguments.of("1999-01-02", "startEventDateTime=lt=1999-01-03", 1),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-02T02:00", 0),
      Arguments.of("1999-01-02T01:00", "startEventDateTime=le=1999-01-02T02:00", 1),
      Arguments.of("1999-01-02T02:00", "startEventDateTime=le=1999-01-02T01:00", 0),

      Arguments.of("1999-01-02T02:00", "startEventDateTime=lt=1999-01-02T02:00", 0),
      Arguments.of("1999-01-02T02:00", "startEventDateTime=lt=1999-01-02T03:00", 1)
    );
  }

  private static Stream<Arguments> gt_FilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("2010", "startEventDateTime=ge=2010", 1),
      Arguments.of("2010", "startEventDateTime=ge=2011", 0),

      Arguments.of("2010", "startEventDateTime=gt=2010", 0),
      Arguments.of("2010", "startEventDateTime=gt=2009", 1),

      // Format YYYY-MM
      Arguments.of("2010", "startEventDateTime=ge=2010-01", 0),
      Arguments.of("2010-01", "startEventDateTime=ge=2010-01", 1),
      Arguments.of("2010-01", "startEventDateTime=ge=2010-02", 0),

      Arguments.of("2010-01", "startEventDateTime=gt=2010-01", 0),
      Arguments.of("2010-01", "startEventDateTime=gt=2009-12", 1),

      // Format YYYY-MM-DD
      Arguments.of("2010-01", "startEventDateTime=ge=2010-01-01", 0),
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-02", 1),
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-03", 0),

      Arguments.of("2010-01-02", "startEventDateTime=gt=2010-01-02", 0),
      Arguments.of("2010-01-02", "startEventDateTime=gt=2010-01-01", 1),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T01:00", "startEventDateTime=ge=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T02:00", "startEventDateTime=ge=2010-01-02T01:00", 1),

      Arguments.of("2010-01-02T02:00", "startEventDateTime=gt=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T02:00", "startEventDateTime=gt=2010-01-02T01:00", 1)
    );
  }

  private static QuerySpec newRsqlQuerySpec(String rsql) {
    QuerySpec spec = new QuerySpec(CollectingEventDto.class);
    spec.addFilter(PathSpec.of("rsql").filter(FilterOperator.EQ, rsql));
    return spec;
  }

}
