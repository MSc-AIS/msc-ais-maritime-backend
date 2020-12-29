package kraptis91.maritime.retriever;

import kraptis91.maritime.model.Vessel;
import kraptis91.maritime.model.VesselTrajectoryChunk;
import kraptis91.maritime.model.VesselTrajectoryPoint;

import java.util.List;
import java.util.Optional;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 1/12/2020. */
public interface MaritimeDataRetriever {

  Optional<String> getVesselDestination(int mmsi);

  Optional<String> getVesselDestination(String vesselName);

  List<VesselTrajectoryChunk> getVesselTrajectory(int mmsi);

  List<VesselTrajectoryChunk> getVesselTrajectory(String vesselName);

  Optional<Vessel> getVesselByMMSI(int mmsi);

  Optional<Vessel> getVesselByName(String vesselName);

  List<Vessel> getVesselsByDestination(String destination, int skip, int limit);

  /**
   * Get vessels by type.
   *
   * @param shipType The ship type
   * @param skip Number of docs to skip in db
   * @param limit Limit the number of results
   * @return The vessel list
   */
  List<Vessel> getVesselsByType(String shipType, int skip, int limit);

  List<Vessel> getVessels(int skip, int limit);

  List<String> getShipTypes();

}
