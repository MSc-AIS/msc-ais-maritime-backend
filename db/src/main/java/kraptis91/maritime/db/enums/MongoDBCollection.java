package kraptis91.maritime.db.enums;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 11/12/2020. */
public enum MongoDBCollection {
  OCEAN_CONDITIONS("oceanConditions"),
  VESSELS("vessels"),
  VESSEL_TRAJECTORY("vesselTrajectories");

  private final String name;

  MongoDBCollection(String name) {
    this.name = name;
  }

  public String getCollectionName() {
    return name;
  }
}
