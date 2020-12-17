package kraptis91.maritime.db.dao.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jakarta.validation.constraints.NotEmpty;
import kraptis91.maritime.db.dao.VesselDao;
import kraptis91.maritime.db.enums.MongoDB;
import kraptis91.maritime.db.enums.MongoDBCollection;
import kraptis91.maritime.model.Vessel;
import kraptis91.maritime.parser.CSVParser;
import kraptis91.maritime.parser.dto.NariStaticDto;
import kraptis91.maritime.parser.enums.MMSICountryCode;
import kraptis91.maritime.parser.enums.ShipTypes;
import kraptis91.maritime.parser.exception.CSVParserException;
import kraptis91.maritime.parser.utils.InputStreamUtils;
import kraptis91.maritime.db.utils.ModelExtractor;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 9/12/2020. */
public class MongoVesselDao implements VesselDao {

  public static final Logger LOGGER = Logger.getLogger(MongoVesselDao.class.getName());

  public static MongoCollection<Vessel> createVesselCollection() {
    return MongoDB.MARITIME
        .getDatabase()
        .getCollection(MongoDBCollection.VESSELS.getCollectionName(), Vessel.class);
  }

  public static MongoCollection<Document> createDocumentCollection() {
    return MongoDB.MARITIME
        .getDatabase()
        .getCollection(MongoDBCollection.VESSELS.getCollectionName(), Document.class);
  }

  @Override
  public void insertMany(@NotNull InputStream csvStream, final int chunkSize) throws Exception {

    // LOGGER.info("Inserting " + csvStream.available() + " bytes to db.");

    final BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(InputStreamUtils.getBufferedInputStream(csvStream)));
    final CSVParser parser = new CSVParser();
    final Set<Vessel> vesselSet = new LinkedHashSet<>(chunkSize);
    final Set<Integer> totalMMSISet = new LinkedHashSet<>();
    int totalVessels = 0;

    // LOGGER.info("Chunk size csvStream " + chunkSize + ".");

    String line;
    NariStaticDto dto;
    boolean isFirstLine = true;

    while ((line = bufferedReader.readLine()) != null) {

      // omit first line
      if (isFirstLine) {
        isFirstLine = false;
        continue;
      }

      try {
        // parse current line to the dto
        dto = parser.extractNariStaticDto(line);

        // System.out.println(dto);
        // check to avoid duplicates
        if (!totalMMSISet.contains(dto.getMmsi())) {
          // add to the list after model obj extraction
          vesselSet.add(
              ModelExtractor.extractVessel(
                  dto,
                  ShipTypes.INSTANCE.getShipType(dto.getShipType()),
                  MMSICountryCode.INSTANCE.getCountryByMMSI(dto.getMmsi())));
          totalMMSISet.add(dto.getMmsi());
        }

        // when read chunkSize number of lines, insert data to mongoDB
        if (vesselSet.size() == chunkSize) {
          // LOGGER.info(vesselSet.size() + " lines read, attempting to insert data to db.");
          insertMany(vesselSet);
          // increase total vessels counter
          totalVessels += vesselSet.size();
          // clear set
          vesselSet.clear();
        }

      } catch (CSVParserException e) {
        // LOGGER.log(Level.WARNING, "Discarding corrupted line " + e.getMessage());
      }
    }
    // LOGGER.info(vesselSet.size() + " lines left, attempting to insert data to db.");
    // insert any data left
    insertMany(vesselSet);
    // increase total vessels counter
    totalVessels += vesselSet.size();
    // clear set
    vesselSet.clear();
    totalMMSISet.clear();

    LOGGER.info("All lines inserted to db successfully.");
    LOGGER.info("Total vessels added to db: " + totalVessels);
  }

  @Override
  public void insertMany(@NotEmpty Set<Vessel> vesselSet) {
    // LOGGER.info("Inserting data to db START.");
    MongoCollection<Vessel> collection = createVesselCollection();
    collection.insertMany(new ArrayList<>(vesselSet));
    // LOGGER.info("Inserting data to db END.");
  }

  @Override
  public void insertMany(@NotEmpty List<Vessel> vesselList) {
    // LOGGER.info("Inserting data to db START.");
    MongoCollection<Vessel> collection = createVesselCollection();
    collection.insertMany(vesselList);
    // LOGGER.info("Inserting data to db END.");
  }

  @Override
  public String findObjectId(int mmsi) {
    Document document =
        createDocumentCollection()
            .find(Filters.eq("mmsi", mmsi))
            .projection(new Document().append("_id", 1))
            .first();

    return Objects.isNull(document) ? null : document.getObjectId("_id").toHexString();
  }

  @Override
  public Optional<Vessel> findVesselByMMSI(int mmsi) {
    return Optional.ofNullable(createVesselCollection().find(Filters.eq("mmsi", mmsi)).first());
  }

  @Override
  public List<Vessel> findVesselsByType(String shipType, int skip, int limit) {
    final List<Vessel> vesselList = new ArrayList<>();
    createVesselCollection()
        .find(Filters.eq("shipType", shipType))
        .skip(skip)
        .limit(limit)
        .forEach((Consumer<Vessel>) vesselList::add);
    return vesselList;
  }

  @Override
  public Optional<Vessel> findVesselByName(String vesselName) {
    return Optional.ofNullable(
        createVesselCollection().find(Filters.eq("vesselName", vesselName)).first());
  }
}
