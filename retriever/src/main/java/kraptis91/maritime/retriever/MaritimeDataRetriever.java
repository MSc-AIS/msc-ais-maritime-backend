package kraptis91.maritime.retriever;

import kraptis91.maritime.model.Vessel;
import kraptis91.maritime.model.VesselTrajectoryPoint;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 1/12/2020. */
public interface MaritimeDataRetriever {

  /**
   * Get vessel Map by time.
   *
   * @param timestamp timestamp in UNIX epochs
   * @return The vessel Map, which has vessel name as key and current position as value
   */
  Map<String, VesselTrajectoryPoint> getVesselPositionMap(long timestamp);

  VesselTrajectoryPoint getVesselPosition(String vesselName, long timestamp);

  String getVesselDestination(String vesselName);

  List<VesselTrajectoryPoint> getVesselTrajectory(String vesselName);

  List<Vessel> getVesselsByTrajectoryPoint(double longitude, double latitude);

  /**
   * Get vessels by speed.
   *
   * @param speed The speed in knots (allowed values: 0-102.2 knots)
   * @return The vessel list
   */
  List<Vessel> getVesselsBySpeed(double speed);

  /**
   * Get vessels by draught specifications.
   *
   * @param min The minimum draught
   * @param max The maximum draught
   * @return The vessel list
   */
  List<Vessel> getVesselsByDraught(double min, double max);

  /**
   * Get vessels by type.
   *
   * @param shipType The ship type
   * @return The vessel list
   */
  List<Vessel> getVesselsByType(int shipType);

  Vessel getVesselByMMSI(int mmsi);

  Vessel getVesselByName(String vesselName);

  List<Vessel> getVesselsByType(String shipType, int skip, int limit);
}
