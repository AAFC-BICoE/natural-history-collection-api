package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.dina.translator.NumberLetterTranslator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MaterialSampleIdentifierGenerator {

  private static final Pattern TRAILING_LETTERS_REGEX = Pattern.compile("([a-zA-Z]+)$");
  private static final Pattern TRAILING_NUMBERS_REGEX = Pattern.compile("(\\d+)$");

  /**
   * This function generates a next identifier based on the provided one.
   * There is no guarantee on the uniqueness.
   * @param providedIdentifier
   * @return
   */
  public String generateNextIdentifier(String providedIdentifier) {

    String currSuffix = "";
    String nextSuffix = "";

    //try letters
    Matcher currMatcher = TRAILING_LETTERS_REGEX.matcher(providedIdentifier);
    if(currMatcher.find()) {
      currSuffix = currMatcher.group(1);

      int matchingNumber = NumberLetterTranslator.toNumber(currSuffix);
      nextSuffix = NumberLetterTranslator.toLetter(matchingNumber + 1);

      // return lowercase but only if the entire suffix was lowercase
      if(StringUtils.isAllLowerCase(currSuffix)){
        nextSuffix = nextSuffix.toLowerCase();
      }
    }

    //otherwise try numbers
    currMatcher = TRAILING_NUMBERS_REGEX.matcher(providedIdentifier);
    if(currMatcher.find()) {
      currSuffix = currMatcher.group(1);
      int nextValue = Integer.parseInt(currSuffix) + 1;

      // preserve 0 left padding if it was there
      nextSuffix = StringUtils.startsWith(currSuffix, "0") ?
          StringUtils.leftPad(Integer.toString(nextValue), currSuffix.length(), "0") :
          Integer.toString(nextValue);
    }

    return StringUtils.removeEnd(providedIdentifier, currSuffix) + nextSuffix;
  }
}
