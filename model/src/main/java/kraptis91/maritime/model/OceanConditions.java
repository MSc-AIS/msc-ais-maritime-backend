package kraptis91.maritime.model;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

/**
 * @author Stavros Lamprinos [stalab at linuxmail.org] on 8/12/2020.
 */
public class OceanConditions {

    /**
     * longitude in degrees (-180.0 to 180.0 range)
     */
    @DecimalMin(value = "-180.0", message = "Invalid value, degrees of longitude cannot be less than -180.0")
    @DecimalMax(value = "180.0", message = "Invalid value, degrees of longitude cannot be more than 180.0")
    private final double longitude;

    /**
     * latitude in degrees (-90.0 to 90.0 range)
     */
    @DecimalMin(value = "-90.0", message = "Invalid value, degrees of latitude cannot be less than -90.0")
    @DecimalMax(value = "90.0", message = "Invalid value, degrees of latitude cannot be more than 90.0")
    private final double latitude;

    /**
     * bottom depth of the oceans point in meters (Undefined value = -16384)
     */
    private final double bottomDepth;

    /**
     * sea surface height above sea level in meters (=>tidal effect). (Undefined value = -327.67)
     */
    private final double tidalEffect;

    /**
     * significant height of wind and swell waves (=>see state) (Undefined value = -65.534)
     */
    private final double seaHeight;

    /**
     * mean wave length in meters. (Undefined value= - 32767)
     */
    private final int meanWaveLength;

    private final long timestamp;


    private OceanConditions(Builder builder) {
        this.longitude = builder.longitude;
        this.latitude = builder.latitude;
        this.bottomDepth = builder.bottomDepth;
        this.tidalEffect = builder.tidalEffect;
        this.seaHeight = builder.seaHeight;
        this.meanWaveLength = builder.meanWaveLength;
        this.timestamp = builder.timestamp;
    }

    // -------------------------------------------------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------------------------------------------------

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getBottomDepth() {
        return bottomDepth;
    }

    public double getTidalEffect() {
        return tidalEffect;
    }

    public double getSeaHeight() {
        return seaHeight;
    }

    public double getMeanWaveLength() {
        return meanWaveLength;
    }

    public long getTimestamp() {
        return timestamp;
    }


    // -------------------------------------------------------------------------------------------------------------------
    // toString
    // -------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "OceanConditions{"
                + "longitude = "
                + longitude
                + ", latitude = "
                + latitude
                + ", bottomDepth = "
                + bottomDepth
                + ", tidalEffect = "
                + tidalEffect
                + ", seaHeight = "
                + seaHeight
                + ", meanWaveLength="
                + meanWaveLength
                + ", timestamp ="
                + timestamp
                + "}";
    }

    // -------------------------------------------------------------------------------------------------------------------
    // Fluent API interfaces
    // -------------------------------------------------------------------------------------------------------------------

    public interface OceanBottomDepth {
        OceanTidalEffect withBottomDepth(double bottomDepth);
    }

    public interface OceanTidalEffect {
        OceanSeaHeight withTidalEffect(double tidalEffect);
    }

    public interface OceanSeaHeight {
        OceanMeanWaveLength withSeaHeight(double seaHeight);
    }

    public interface OceanMeanWaveLength {
        OceanBuild withMeanWaveLength(int meanWaveLength);
    }

    public interface OceanBuild {
        OceanConditions build();
    }

    // -------------------------------------------------------------------------------------------------------------------
    // Vessel POJO builder
    // -------------------------------------------------------------------------------------------------------------------

    public static class Builder
            implements OceanBottomDepth,
            OceanTidalEffect,
            OceanSeaHeight,
            OceanMeanWaveLength,
            OceanBuild {

        // mandatory fields
        private final double longitude;
        private final double latitude;
        private final long timestamp;

        // optional fields
        private double bottomDepth;
        private double tidalEffect;
        private double seaHeight;
        private int meanWaveLength;

        public Builder(double longitude, double latitude, long timestamp) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.timestamp = timestamp;
        }

        /**
         * @param bottomDepth The depth of the oceans point in meters (Undefined value = -16384)
         * @return The Builder
         */
        @Override
        public OceanTidalEffect withBottomDepth(double bottomDepth) {
            // this.bottomDepth = OptionalDouble.of(bottomDepth).orElse(-16384.0);
            this.bottomDepth = bottomDepth;
            return this;
        }

        /**
         * @param seaHeight Sea surface height above sea level in meters (Undefined value = -65.534)
         * @return The Builder
         */
        @Override
        public OceanMeanWaveLength withSeaHeight(double seaHeight) {
            // this.seaHeight = OptionalDouble.of(seaHeight).orElse(-65.534);
            this.seaHeight = seaHeight;
            return this;
        }

        /**
         * @param meanWaveLength Mean wave length in meters (Undefined value = -32767)
         * @return The Builder
         */
        @Override
        public OceanBuild withMeanWaveLength(int meanWaveLength) {
            // this.meanWaveLength = OptionalInt.of(meanWaveLength).orElse(-32767);
            this.meanWaveLength = meanWaveLength;
            return this;
        }

        /**
         * @param tidalEffect Significant height of wind and swell waves (Undefined value = -327.67)
         * @return The Builder
         */
        @Override
        public OceanSeaHeight withTidalEffect(double tidalEffect) {
            // this.tidalEffect = OptionalDouble.of(tidalEffect).orElse(-327.67);
            this.tidalEffect = tidalEffect;
            return this;
        }

        @Override
        public OceanConditions build() {
            return new OceanConditions(this);
        }
    }
}
