package ca.gc.aafc.collection.api.service;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaterialSampleIdentifierGenerator {

  private static final Pattern TRAILING_LETTERS_REGEX = Pattern.compile("([a-zA-Z]+)$");
  private static final Pattern TRAILING_NUMBERS_REGEX = Pattern.compile("(\\d+)$");

  /**
   * This function gives the next identifier based on the provided one.
   * There is no guarantee on the uniqueness.
   * @param currentIdentifier
   * @return
   */
  public String getNext (String currentIdentifier) {

    String currSuffix = "";
    String nextSuffix = "";

    //try letters
    Matcher currMatcher = TRAILING_LETTERS_REGEX.matcher(currentIdentifier);
    if(currMatcher.find()) {
      currSuffix = currMatcher.group(1);

      // convert to number
      // increment
      // convert back

    }

    //otherwise try numbers
    currMatcher = TRAILING_NUMBERS_REGEX.matcher(currentIdentifier);
    if(currMatcher.find()) {
      currSuffix = currMatcher.group(1);
      int nextValue = Integer.parseInt(currSuffix) + 1;

      // preserve 0 left padding if it was there
      nextSuffix = StringUtils.startsWith(currSuffix, "0") ?
          StringUtils.leftPad(Integer.toString(nextValue), currSuffix.length(), "0") :
          Integer.toString(nextValue);
    }

    return StringUtils.removeEnd(currentIdentifier, currSuffix) + nextSuffix;
  }
}
