package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.entities.Determination;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class DeterminationFixture {

  public static Determination newDetermination() throws MalformedURLException {
    return Determination.builder()
        .verbatimScientificName("verbatimScientificName")
        .verbatimDeterminer("verbatimDeterminer")
        .verbatimDate("2021-01-01")
        .scientificName("scientificName")
        .transcriberRemarks("transcriberRemarks")
        .verbatimRemarks("verbatimRemarks")
        .determinationRemarks("determinationRemarks")
        .isPrimary(true)
        .typeStatus("typeStatus")
        .typeStatusEvidence("typeStatusEvidence")
        .determiner(List.of(UUID.randomUUID()))
        .determinedOn(LocalDate.now())
        .qualifier("qualifier")
        .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
        .scientificNameDetails(Determination.ScientificNameSourceDetails.builder()
            .currentName("scientificName")
            .isSynonym(true)
            .classificationPath("classificationPath")
            .classificationRanks("classificationRanks")
            .sourceUrl(new URL("https://www.google.com").toString())
            .recordedOn(LocalDate.now().minusDays(1))
            .labelHtml("label")
            .build())
        .isFileAs(true)
        .build();
  }
}
