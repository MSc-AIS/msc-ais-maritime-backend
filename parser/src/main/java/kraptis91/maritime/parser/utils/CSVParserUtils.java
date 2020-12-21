package kraptis91.maritime.parser.utils;

import kraptis91.maritime.parser.exception.CSVParserException;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Logger;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 11/12/2020. */
public class CSVParserUtils {

  public static final Logger LOGGER = Logger.getLogger(CSVParserUtils.class.getName());

  public static double parseDouble(@NotNull String value)
      throws CSVParserException, IllegalArgumentException {
    validateValue("parseDouble", value);

    try {
      return Double.parseDouble(trimAndRemoveJunk(value));
    } catch (NumberFormatException e) {
      throw new CSVParserException(e);
    }
  }

  public static double parseDoubleOrReturnDefault(@NotNull String value, double defaultValue) {
    try {
      validateValue("parseDoubleOrReturnDefault", value);
      return Double.parseDouble(trimAndRemoveJunk(value));
    } catch (IllegalArgumentException e) { // NumberFormatException also will be caught here
      return defaultValue;
    }
  }

  public static int parseIntOrReturnDefault(@NotNull String value, int defaultValue) {
    try {
      validateValue("parseIntOrReturnDefault", value);
      return Integer.parseInt(trimAndRemoveJunk(value));
    } catch (IllegalArgumentException e) { // NumberFormatException also will be caught here
      return defaultValue;
    }
  }

  public static int parseInt(@NotNull String value)
      throws CSVParserException, IllegalArgumentException {
    validateValue("parseInt", value);

    try {
      return Integer.parseInt(trimAndRemoveJunk(value));
    } catch (NumberFormatException e) {
      throw new CSVParserException(e);
    }
  }

  public static long parseLong(@NotNull String value)
      throws CSVParserException, IllegalArgumentException {
    validateValue("parseLong", value);

    try {
      return Long.parseLong(trimAndRemoveJunk(value));
    } catch (NumberFormatException e) {
      throw new CSVParserException(e);
    }
  }

  public static String parseText(@NotNull String value) throws IllegalArgumentException {
    validateValue("parseText", value);
    return trimAndRemoveJunk(value);
  }

  @Nullable
  public static String parseTextOrReturnNull(@Nullable String value) {

    if (Objects.isNull(value) || value.isEmpty() || value.isBlank()) {
      return null;
    }

    return trimAndRemoveJunk(value);
  }

  /** @param name The name of the method call this method */
  private static void validateValue(String name, String value) throws IllegalArgumentException {

    if (Objects.isNull(value)) {
      throw new IllegalArgumentException("Error... Trying to " + name + " null value");
    } else if (value.isEmpty()) {
      throw new IllegalArgumentException("Error... Trying to " + name + " empty value");
    } else if (value.isBlank()) {
      throw new IllegalArgumentException("Error... Trying to " + name + " blank value");
    }
  }

  public static String trimAndRemoveJunk(@NotNull String value) {
    value = value.replaceAll("\"", "");
    value = value.trim();
    return value;
  }

  public static String[] splitLineAtCommas(@NotNull String line) {
    return line.split(",");
  }

  public static String replaceAllCommasInQuotesWith(@NotNull String line, String with) {
    return line.replaceAll("(\".*?),(.*?\")", with);
  }

  public static String replaceAllQuotesWith(@NotNull String line, String with) {
    return line.replaceAll("\"", with);
  }

  public static String[] parseLine(@NotNull String line) {
    //    return Arrays.stream(splitLineAtCommas(replaceAllCommasInQuotesWith(line, "$1 $2")))
    //        .map(v -> replaceAllQuotesWith(v, ""))
    //        .toArray(String[]::new);
    return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
  }
}
