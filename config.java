import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class Config
{
    private Map<String, String> baseFares;
    private Map<String, String> discountRates;
    private Map<String, String> dailyCaps;
    private String peakWindowsDescription;

    public Config()
    {
        baseFares = new HashMap<>();
        discountRates = new HashMap<>();
        dailyCaps = new HashMap<>();
        peakWindowsDescription = "07:00-09:30 and 16:00-19:00";
    }

    public BigDecimal getBaseFare(int from, int to, CityRideDataset.TimeBand band)
    {
        String key = from + "-" + to + "-" + band.name();
        String value = baseFares.get(key);
        return value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }

    public void setBaseFare(int from, int to, CityRideDataset.TimeBand band, BigDecimal fare)
    {
        baseFares.put(from + "-" + to + "-" + band.name(), fare.toPlainString());
    }

    public BigDecimal getDiscountRate(CityRideDataset.PassengerType type)
    {
        String value = discountRates.get(type.name());
        return value != null ? new BigDecimal(value) : BigDecimal.ZERO;
    }

    public void setDiscountRate(CityRideDataset.PassengerType type, BigDecimal rate)
    {
        discountRates.put(type.name(), rate.toPlainString());
    }

    public BigDecimal getDailyCap(CityRideDataset.PassengerType type)
    {
        String value = dailyCaps.get(type.name());
        return value != null ? new BigDecimal(value) : new BigDecimal("999.99");
    }

    public void setDailyCap(CityRideDataset.PassengerType type, BigDecimal cap)
    {
        dailyCaps.put(type.name(), cap.toPlainString());
    }

    public Map<CityRideDataset.PassengerType, BigDecimal> getDailyCaps()
    {
        Map<CityRideDataset.PassengerType, BigDecimal> result = new HashMap<>();
        for (CityRideDataset.PassengerType t : CityRideDataset.PassengerType.values())
        {
            result.put(t, getDailyCap(t));
        }
        return result;
    }

    public String getPeakWindowsDescription() { return peakWindowsDescription; }
    public void setPeakWindowsDescription(String description) { this.peakWindowsDescription = description; }

    Map<String, String> getBaseFaresMap() { return baseFares; }
    Map<String, String> getDiscountRatesMap() { return discountRates; }
    Map<String, String> getDailyCapsMap() { return dailyCaps; }
}
