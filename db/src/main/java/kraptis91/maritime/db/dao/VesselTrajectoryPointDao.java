package kraptis91.maritime.db.dao;

import kraptis91.maritime.model.VesselTrajectoryPoint;

import java.io.InputStream;
import java.util.List;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 13/12/2020. */
public interface VesselTrajectoryPointDao {

  void insertMany(InputStream csvStream, int chunkSize) throws Exception;

  default void insertMany(InputStream is) throws Exception {
    insertMany(is, 3000);
  }

  void insertMany(List<VesselTrajectoryPoint> points);
}
