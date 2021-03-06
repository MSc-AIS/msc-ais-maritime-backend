package kraptis91.maritime.parser.enums;

import kraptis91.maritime.parser.CSVParser;
import kraptis91.maritime.parser.dto.csv.MMSICountryCodesDto;
import kraptis91.maritime.parser.exception.CSVParserException;
import kraptis91.maritime.parser.utils.InputStreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 13/12/2020. */
public enum MMSICountryCode {
  INSTANCE("/csv/mmsiCountryCodes.csv");

  public final Logger LOGGER = java.util.logging.Logger.getLogger(MMSICountryCode.class.getName());
  private final Map<String, String> mmsiCountryCodeMap;

  MMSICountryCode(String resource) {

    mmsiCountryCodeMap = new LinkedHashMap<>();

    try {
      LOGGER.info("Initializing MMSI Country Codes from " + resource + " START");
      final InputStream is = MMSICountryCode.class.getResourceAsStream(resource);
      final BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(InputStreamUtils.getBufferedInputStream(is)));
      final CSVParser parser = new CSVParser();

      String line;
      MMSICountryCodesDto dto;
      boolean isFirstLine = true;

      while ((line = bufferedReader.readLine()) != null) {

        // omit first line
        if (isFirstLine) {
          isFirstLine = false;
          continue;
        }

        // parse current line to the dto
        dto = parser.extractMMSICountryCodesDto(line);
        // System.out.println(dto);
        // add mmsi country code as key to Map and country name as value
        mmsiCountryCodeMap.put(String.valueOf(dto.getMmsiCountryCode()), dto.getCountry());
      }

      LOGGER.info("Initializing MMSI Country Codes from " + resource + " END");

    } catch (IOException | CSVParserException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  public String getCountryByMMSI(int mmsi) {
    String mmsiAsString = String.valueOf(mmsi);

    if (mmsiAsString.length() < 3) {
      throw new IllegalArgumentException("Error... MMSI country code length must be >= 3");
    }
    String mmsiCountryCode = mmsiAsString.substring(0, 3);
    return mmsiCountryCodeMap.get(mmsiCountryCode);
  }
}
