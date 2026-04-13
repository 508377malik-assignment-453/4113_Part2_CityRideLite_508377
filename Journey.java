import java.math.BigDecimal;
import java.math.RoundingMode;

public class Journey
{
    private int id;
    private String date;
    private int fromZone;
    private int toZone;
    private CityRideDataset.PassengerType passengerType;
    private CityRideDataset.TimeBand timeBand;
    private int zonesCrossed;
    private BigDecimal baseFare;
    private BigDecimal discountedFare;
    private BigDecimal chargedFare;

    public Journey(int id, String date, int fromZone, int toZone,
                   CityRideDataset.PassengerType passengerType,
                   CityRideDataset.TimeBand timeBand, int zonesCrossed,
                   BigDecimal baseFare, BigDecimal discountedFare, BigDecimal chargedFare)
    {
        this.id = id;
        this.date = date;
        this.fromZone = fromZone;
        this.toZone = toZone;
        this.passengerType = passengerType;
        this.timeBand = timeBand;
        this.zonesCrossed = zonesCrossed;
        this.baseFare = baseFare;
        this.discountedFare = discountedFare;
        this.chargedFare = chargedFare;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getFromZone() { return fromZone; }
    public void setFromZone(int fromZone) { this.fromZone = fromZone; }
    public int getToZone() { return toZone; }
    public void setToZone(int toZone) { this.toZone = toZone; }
    public CityRideDataset.PassengerType getPassengerType() { return passengerType; }
    public CityRideDataset.TimeBand getTimeBand() { return timeBand; }
    public void setTimeBand(CityRideDataset.TimeBand band)  { this.timeBand = band; }
    public int getZonesCrossed() { return zonesCrossed; }
    public void setZonesCrossed(int zonesCrossed) { this.zonesCrossed = zonesCrossed; }
    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }
    public BigDecimal getDiscountedFare() { return discountedFare; }
    public void setDiscountedFare(BigDecimal discountedFare){ this.discountedFare = discountedFare; }
    public BigDecimal getChargedFare() { return chargedFare; }
    public void setChargedFare(BigDecimal chargedFare)  { this.chargedFare = chargedFare; }

    @Override
    public String toString() {
        String bandLabel = timeBand == CityRideDataset.TimeBand.PEAK ? "Peak" : "Off-peak";
        return "ID: " + id
                + " | Date: " + date
                + " | Zone " + fromZone + "->" + toZone
                + " | Zones: " + zonesCrossed
                + " | " + bandLabel
                + " | " + passengerType
                + " | Base: £" + baseFare
                + " | Charged: £" + chargedFare;
    }
}

