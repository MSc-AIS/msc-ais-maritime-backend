package kraptis91.maritime.db.dao.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import kraptis91.maritime.db.dao.DaoFactory;
import kraptis91.maritime.db.dao.VesselDao;
import kraptis91.maritime.db.dao.VesselTrajectoryPointDao;
import kraptis91.maritime.db.enums.MongoDB;
import kraptis91.maritime.db.enums.MongoDBCollection;
import kraptis91.maritime.db.utils.ModelExtractor;
import kraptis91.maritime.model.VesselTrajectoryPoint;
import kraptis91.maritime.parser.CSVParser;
import kraptis91.maritime.parser.dto.NariDynamicDto;
import kraptis91.maritime.parser.exception.CSVParserException;
import kraptis91.maritime.parser.utils.InputStreamUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 13/12/2020. */
public class MongoVesselTrajectoryPointDao implements VesselTrajectoryPointDao {

  public static final Logger LOGGER =
      Logger.getLogger(MongoVesselTrajectoryPointDao.class.getName());

  public static MongoCollection<VesselTrajectoryPoint> createVesselTrajectoryPointsCollection() {
    return MongoDB.MARITIME
        .getDatabase()
        .getCollection(
            MongoDBCollection.VESSEL_TRAJECTORY_POINTS.getCollectionName(),
            VesselTrajectoryPoint.class);
  }

  @Override
  public void insertMany(InputStream csvStream, int chunkSize) throws Exception {

    // LOGGER.info("Inserting " + is.available() + " bytes to db.");

    final BufferedReader bufferedReader =
        new BufferedReader(
            new InputStreamReader(InputStreamUtils.getBufferedInputStream(csvStream)));
    final CSVParser parser = new CSVParser();
    final List<VesselTrajectoryPoint> pointList = new ArrayList<>(chunkSize);
    // key = mmsi, value = ObjectId as a hex String
    final Map<Integer, String> vesselIdMap = new LinkedHashMap<>();
    // Vessel dao to find ObjectId of vessels
    final VesselDao vesselDao = DaoFactory.createMongoVesselDao();
    int totalPoints = 0;

    // LOGGER.info("Chunk size is " + chunkSize + ".");

    String line;
    NariDynamicDto dto;
    boolean isFirstLine = true;

    while ((line = bufferedReader.readLine()) != null) {

      // omit first line
      if (isFirstLine) {
        isFirstLine = false;
        continue;
      }

      try {

        // parse current line to the dto
        dto = parser.extractNariDynamicDto(line);
        // before add find vessel ObjectId
        if (vesselIdMap.containsKey(dto.getMmsi())) {

          // get it from map
          String objectId = vesselIdMap.get(dto.getMmsi());

          if (!Objects.isNull(objectId)) {
            // add to the list after model obj extraction
            pointList.add(ModelExtractor.extractVesselTrajectoryPoint(dto, objectId));
          } else {
            //            LOGGER.log(
            //                Level.WARNING,
            //                "ObjectId retrieved from Map but was null for mmsi " + dto.getMmsi());
          }

        } else {

          // get it from db
          Optional<String> objectId = vesselDao.findObjectIdAsString(dto.getMmsi());

          // if (!Objects.isNull(objectId)) {
          if (objectId.isPresent()) {

            // add to the list after model obj extraction
            pointList.add(ModelExtractor.extractVesselTrajectoryPoint(dto, objectId.get()));
            // add id to vesselIdMap
            vesselIdMap.put(dto.getMmsi(), objectId.get());

          } else { // a vessel not found for given mmsi

            // add it to map as null to avoid to search again db
            vesselIdMap.put(dto.getMmsi(), null);
            // LOGGER.log(Level.WARNING, "Cannot find a vessel for mmsi " + dto.getMmsi());
          }
        }

        // when read chunkSize number of lines, insert data to mongoDB
        if (pointList.size() == chunkSize) {
          // LOGGER.info(vesselSet.size() + " lines read, attempting to insert data to db.");
          insertMany(pointList);
          // increase total vessels counter
          totalPoints += pointList.size();
          // clear set
          pointList.clear();
        }

      } catch (CSVParserException e) {
        // LOGGER.log(Level.WARNING, "Discarding corrupted line" + e.getMessage());
      }
    }
    // LOGGER.info(vesselSet.size() + " lines left, attempting to insert data to db.");
    // insert any data left
    insertMany(pointList);
    // increase total vessels counter
    totalPoints += pointList.size();
    // clear set
    pointList.clear();
    // clear vesselId map
    vesselIdMap.clear();

    LOGGER.info("All lines inserted to db successfully.");
    LOGGER.info("Total vessel trajectory points added to db: " + totalPoints);
  }

  @Override
  public void insertMany(List<VesselTrajectoryPoint> points) {
    // LOGGER.info("Inserting data to db START.");
    MongoCollection<VesselTrajectoryPoint> collection = createVesselTrajectoryPointsCollection();
    collection.insertMany(points);
    // LOGGER.info("Inserting data to db END.");
  }

  @Override
  public List<VesselTrajectoryPoint> findVesselTrajectory(String vesselName, int skip, int limit) {
    final List<VesselTrajectoryPoint> trajectoryPointList = new ArrayList<>();
    createVesselTrajectoryPointsCollection()
        .aggregate(
            Arrays.asList(
                Aggregates.lookup("vessels", "vesselId", "_id", "vesselTrajectory"),
                Aggregates.match(Filters.eq("vesselTrajectory.vesselName", vesselName)),
                Aggregates.skip(skip),
                Aggregates.limit(limit)))
        .forEach((Consumer<VesselTrajectoryPoint>) trajectoryPointList::add);
    return trajectoryPointList;
  }

  @Override
  public List<VesselTrajectoryPoint> findVesselTrajectory(int mmsi, int skip, int limit) {
    final List<VesselTrajectoryPoint> trajectoryPointList = new ArrayList<>();
    createVesselTrajectoryPointsCollection()
        .aggregate(
            Arrays.asList(
                Aggregates.lookup("vessels", "vesselId", "_id", "vesselTrajectory"),
                Aggregates.match(Filters.eq("vesselTrajectory.mmsi", mmsi)),
                Aggregates.skip(skip),
                Aggregates.limit(limit)))
        .forEach((Consumer<VesselTrajectoryPoint>) trajectoryPointList::add);
    return trajectoryPointList;
  }
}
