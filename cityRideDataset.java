import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

final class CityRideDataset {

    private CityRideDataset() {}

    public static final int MIN_ZONE = 1;
    public static final int MAX_ZONE = 5;

    public enum TimeBand
    {
        PEAK,
        OFF_PEAK
    }

    public enum PassengerType
    {
        ADULT,
        STUDENT,
        CHILD,
        SENIOR_CITIZEN
    }

    public static Map<PassengerType, BigDecimal> DISCOUNT_RATE = buildDefaultDiscountRates();

    public static Map<PassengerType, BigDecimal> DAILY_CAP = buildDefaultDailyCaps();

    public static Map<String, BigDecimal> BASE_FARE = buildDefaultBaseFares();

    public static BigDecimal getBaseFare(int fromZone, int toZone, TimeBand timeBand)
    {
        return BASE_FARE.get(fareKey(fromZone, toZone, timeBand));
    }

    public static String fareKey(int fromZone, int toZone, TimeBand timeBand)
    {
        return fromZone + "-" + toZone + "-" + timeBand.name();
    }

    public static Map<PassengerType, BigDecimal> buildDefaultDiscountRates()
    {
        Map<PassengerType, BigDecimal> rates = new HashMap<>();
        rates.put(PassengerType.ADULT, money("0.00"));
        rates.put(PassengerType.STUDENT, money("0.25"));
        rates.put(PassengerType.CHILD, money("0.50"));
        rates.put(PassengerType.SENIOR_CITIZEN, money("0.30"));
        return rates;
    }

    public static Map<PassengerType, BigDecimal> buildDefaultDailyCaps()
    {
        Map<PassengerType, BigDecimal> caps = new HashMap<>();
        caps.put(PassengerType.ADULT, money("8.00"));
        caps.put(PassengerType.STUDENT, money("6.00"));
        caps.put(PassengerType.CHILD, money("4.00"));
        caps.put(PassengerType.SENIOR_CITIZEN, money("7.00"));
        return caps;
    }

    public static Map<String, BigDecimal> buildDefaultBaseFares()
    {

        Map<String, BigDecimal> fares = new HashMap<>();

        addFare(fares, 1, 1, TimeBand.PEAK, "2.50"); addFare(fares, 1, 2, TimeBand.PEAK, "3.20");
        addFare(fares, 1, 3, TimeBand.PEAK, "3.80"); addFare(fares, 1, 4, TimeBand.PEAK, "4.40");
        addFare(fares, 1, 5, TimeBand.PEAK, "5.00");

        addFare(fares, 2, 1, TimeBand.PEAK, "3.20"); addFare(fares, 2, 2, TimeBand.PEAK, "2.30");
        addFare(fares, 2, 3, TimeBand.PEAK, "3.10"); addFare(fares, 2, 4, TimeBand.PEAK, "3.80");
        addFare(fares, 2, 5, TimeBand.PEAK, "4.50");

        addFare(fares, 3, 1, TimeBand.PEAK, "3.80"); addFare(fares, 3, 2, TimeBand.PEAK, "3.10");
        addFare(fares, 3, 3, TimeBand.PEAK, "2.10"); addFare(fares, 3, 4, TimeBand.PEAK, "3.00");
        addFare(fares, 3, 5, TimeBand.PEAK, "3.70");

        addFare(fares, 4, 1, TimeBand.PEAK, "4.40"); addFare(fares, 4, 2, TimeBand.PEAK, "3.80");
        addFare(fares, 4, 3, TimeBand.PEAK, "3.00"); addFare(fares, 4, 4, TimeBand.PEAK, "2.00");
        addFare(fares, 4, 5, TimeBand.PEAK, "2.90");

        addFare(fares, 5, 1, TimeBand.PEAK, "5.00"); addFare(fares, 5, 2, TimeBand.PEAK, "4.50");
        addFare(fares, 5, 3, TimeBand.PEAK, "3.70"); addFare(fares, 5, 4, TimeBand.PEAK, "2.90");
        addFare(fares, 5, 5, TimeBand.PEAK, "1.90");

        addFare(fares, 1, 1, TimeBand.OFF_PEAK, "2.00"); addFare(fares, 1, 2, TimeBand.OFF_PEAK, "2.70");
        addFare(fares, 1, 3, TimeBand.OFF_PEAK, "3.20"); addFare(fares, 1, 4, TimeBand.OFF_PEAK, "3.70");
        addFare(fares, 1, 5, TimeBand.OFF_PEAK, "4.20");

        addFare(fares, 2, 1, TimeBand.OFF_PEAK, "2.70"); addFare(fares, 2, 2, TimeBand.OFF_PEAK, "1.90");
        addFare(fares, 2, 3, TimeBand.OFF_PEAK, "2.60"); addFare(fares, 2, 4, TimeBand.OFF_PEAK, "3.20");
        addFare(fares, 2, 5, TimeBand.OFF_PEAK, "3.80");

        addFare(fares, 3, 1, TimeBand.OFF_PEAK, "3.20"); addFare(fares, 3, 2, TimeBand.OFF_PEAK, "2.60");
        addFare(fares, 3, 3, TimeBand.OFF_PEAK, "1.70"); addFare(fares, 3, 4, TimeBand.OFF_PEAK, "2.50");
        addFare(fares, 3, 5, TimeBand.OFF_PEAK, "3.10");

        addFare(fares, 4, 1, TimeBand.OFF_PEAK, "3.70"); addFare(fares, 4, 2, TimeBand.OFF_PEAK, "3.20");
        addFare(fares, 4, 3, TimeBand.OFF_PEAK, "2.50"); addFare(fares, 4, 4, TimeBand.OFF_PEAK, "1.60");
        addFare(fares, 4, 5, TimeBand.OFF_PEAK, "2.40");

        addFare(fares, 5, 1, TimeBand.OFF_PEAK, "4.20"); addFare(fares, 5, 2, TimeBand.OFF_PEAK, "3.80");
        addFare(fares, 5, 3, TimeBand.OFF_PEAK, "3.10"); addFare(fares, 5, 4, TimeBand.OFF_PEAK, "2.40");
        addFare(fares, 5, 5, TimeBand.OFF_PEAK, "1.50");

        return fares;
    }

    private static void addFare(Map<String, BigDecimal> map, int from, int to,
                                TimeBand band, String amount)
    {
        map.put(fareKey(from, to, band), money(amount));
    }

    public static BigDecimal money(String amount)
    {
        return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
    }
}

