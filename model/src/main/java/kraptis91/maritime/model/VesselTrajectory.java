package kraptis91.maritime.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** @author Konstantinos Raptis [kraptis at unipi.gr] on 21/12/2020. */
public class VesselTrajectory {

  private String uuid;
  private final int mmsi;
  private final String vesselName;
  private final String shipType;
  private final String destination;
  private final Date startDate;
  private final Date endDate;
  private List<VesselTrajectoryPoint> pointList;
  private GeoPoint avgGeoPoint;
  private double avgSpeed;

  private VesselTrajectory(Builder builder) {
    this.mmsi = builder.mmsi;
    this.vesselName = builder.vesselName;
    this.shipType = builder.shipType;
    this.destination = builder.destination;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
  }

  private VesselTrajectory(FluentBuilder builder) {
    this.mmsi = builder.mmsi;
    this.vesselName = builder.vesselName;
    this.shipType = builder.shipType;
    this.destination = builder.destination;
    this.startDate = builder.startDate;
    this.endDate = builder.endDate;
    this.avgGeoPoint = builder.avgGeoPoint;
    this.avgSpeed = builder.avgSpeed;
  }

  public List<VesselTrajectoryPoint> getPointList() {
    if (Objects.isNull(pointList)) {
      pointList = new ArrayList<>();
    }
    return pointList;
  }

  public void setAvgGeoPoint(GeoPoint avgGeoPoint) {
    this.avgGeoPoint = avgGeoPoint;
  }

  public void setAvgSpeed(double avgSpeed) {
    this.avgSpeed = avgSpeed;
  }

  public double getAvgSpeed() {
    return avgSpeed;
  }

  public double calcAvgSpeed() {
    return pointList.stream()
        .mapToDouble(VesselTrajectoryPoint::getSpeed)
        .average()
        .orElse(Double.NaN);
  }

  public GeoPoint calcAvgGeoPoint() {
    return extractAvgGeoPoint(
        pointList.stream().map(VesselTrajectoryPoint::getGeoPoint).collect(Collectors.toList()));
  }

  public GeoPoint getAvgGeoPoint() {
    return avgGeoPoint;
  }

  public static GeoPoint extractAvgGeoPoint(List<GeoPoint> geoPointList) {
    return GeoPoint.of(
        geoPointList.stream()
            .mapToDouble(p -> p.getCoordinates().get(0)) // longitude
            .average()
            .orElse(Double.NaN),
        geoPointList.stream()
            .mapToDouble(p -> p.getCoordinates().get(1)) // latitude
            .average()
            .orElse(Double.NaN));
  }

  public int getNumberOfPoints() {
    return getPointList().size();
  }

  public Date getStartDate() {
    return startDate;
  }

  public int getMmsi() {
    return mmsi;
  }

  public String getVesselName() {
    return vesselName;
  }

  public String getShipType() {
    return shipType;
  }

  public String getDestination() {
    return destination;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setUUID(String uuid) {
    this.uuid = uuid;
  }

  public String getUUID() {
    return uuid;
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Fluent API interfaces
  // -------------------------------------------------------------------------------------------------------------------

  public interface TrajectoryVesselName {
    TrajectoryShipType withVesselName(String vesselName);
  }

  public interface TrajectoryShipType {
    TrajectoryDestination withShipType(String shipType);
  }

  public interface TrajectoryDestination {
    TrajectoryStartDate withDestination(String destination);
  }

  public interface TrajectoryStartDate {
    TrajectoryEndDate withStartDate(Date date);
  }

  public interface TrajectoryEndDate {
    TrajectoryAvgGeoPoint withEndDate(Date date);
  }

  public interface TrajectoryAvgGeoPoint {
    TrajectoryAvgSpeed withAvgGeoPoint(GeoPoint geoPoint);
  }

  public interface TrajectoryAvgSpeed {
    TrajectoryBuild withAvgSpeed(double speed);
  }

  public interface TrajectoryBuild {
    VesselTrajectory build();
  }

  // -------------------------------------------------------------------------------------------------------------------
  // VesselTrajectory POJO builders
  // -------------------------------------------------------------------------------------------------------------------

  public static Builder builder(int mmsi) {
    return new Builder(mmsi);
  }

  public static FluentBuilder fluentBuilder(int mmsi) {
    return new FluentBuilder(mmsi);
  }

  public static class FluentBuilder
      implements TrajectoryVesselName,
          TrajectoryShipType,
          TrajectoryDestination,
          TrajectoryStartDate,
          TrajectoryEndDate,
          TrajectoryAvgGeoPoint,
          TrajectoryAvgSpeed,
          TrajectoryBuild {

    private final int mmsi;
    private String vesselName;
    private String shipType;
    private String destination;
    private Date startDate;
    private Date endDate;
    private GeoPoint avgGeoPoint;
    private double avgSpeed;

    public FluentBuilder(int mmsi) {
      this.mmsi = mmsi;
    }

    @Override
    public TrajectoryShipType withVesselName(String vesselName) {
      this.vesselName = vesselName;
      return this;
    }

    @Override
    public TrajectoryDestination withShipType(String shipType) {
      this.shipType = shipType;
      return this;
    }

    @Override
    public TrajectoryStartDate withDestination(String destination) {
      this.destination = destination;
      return this;
    }

    @Override
    public TrajectoryEndDate withStartDate(Date date) {
      this.startDate = date;
      return this;
    }

    @Override
    public TrajectoryAvgGeoPoint withEndDate(Date date) {
      this.endDate = date;
      return this;
    }

    @Override
    public TrajectoryAvgSpeed withAvgGeoPoint(GeoPoint geoPoint) {
      this.avgGeoPoint = geoPoint;
      return this;
    }

    @Override
    public TrajectoryBuild withAvgSpeed(double speed) {
      this.avgSpeed = speed;
      return this;
    }

    @Override
    public VesselTrajectory build() {
      return new VesselTrajectory(this);
    }
  }

  public static class Builder {

    private final int mmsi;
    private String vesselName;
    private String shipType;
    private String destination;
    private Date startDate;
    private Date endDate;

    public Builder(int mmsi) {
      this.mmsi = mmsi;
    }

    public Builder withVesselName(String vesselName) {
      this.vesselName = vesselName;
      return this;
    }

    public Builder withShipType(String shipType) {
      this.shipType = shipType;
      return this;
    }

    public Builder withDestination(String destination) {
      this.destination = destination;
      return this;
    }

    public Builder withStartDate(Date date) {
      this.startDate = date;
      return this;
    }

    public Builder withEndDate(Date date) {
      this.endDate = date;
      return this;
    }

    public VesselTrajectory build() {
      return new VesselTrajectory(this);
    }
  }
}
