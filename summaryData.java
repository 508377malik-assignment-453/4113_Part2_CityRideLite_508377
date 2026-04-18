import java.math.BigDecimal;
import java.util.Map;

public class SummaryData
{
    private int totalJourneys;
    private BigDecimal totalCharged;
    private BigDecimal averageFare;
    private Journey mostExpensive;
    private int peakCount;
    private int offPeakCount;
    private BigDecimal savings;
    private Map<String, Integer> zonePairCounts;
    private Map<CityRideDataset.PassengerType, BigDecimal> runningTotals;
    private Map<CityRideDataset.PassengerType, BigDecimal> dailyCaps;

    public SummaryData(int totalJourneys, BigDecimal totalCharged, BigDecimal averageFare,
                       Journey mostExpensive, int peakCount, int offPeakCount,
                       BigDecimal savings, Map<String, Integer> zonePairCounts,
                       Map<CityRideDataset.PassengerType, BigDecimal> runningTotals,
                       Map<CityRideDataset.PassengerType, BigDecimal> dailyCaps)
    {
        this.totalJourneys = totalJourneys;
        this.totalCharged = totalCharged;
        this.averageFare = averageFare;
        this.mostExpensive = mostExpensive;
        this.peakCount = peakCount;
        this.offPeakCount = offPeakCount;
        this.savings = savings;
        this.zonePairCounts = zonePairCounts;
        this.runningTotals = runningTotals;
        this.dailyCaps = dailyCaps;
    }

    public int getTotalJourneys() { return totalJourneys; }
    public BigDecimal getTotalCharged() { return totalCharged; }
    public BigDecimal getAverageFare() { return averageFare; }
    public Journey getMostExpensive() { return mostExpensive; }
    public int getPeakCount() { return peakCount; }
    public int getOffPeakCount() { return offPeakCount; }
    public BigDecimal getSavings() { return savings; }
    public Map<String, Integer> getZonePairCounts() { return zonePairCounts; }
    public Map<CityRideDataset.PassengerType, BigDecimal> getRunningTotals(){ return runningTotals; }
    public Map<CityRideDataset.PassengerType, BigDecimal> getDailyCaps() { return dailyCaps; }
}
