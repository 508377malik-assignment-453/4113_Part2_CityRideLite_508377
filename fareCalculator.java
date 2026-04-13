import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareCalculator
{
    public static BigDecimal applyDiscount(BigDecimal baseFare, CityRideDataset.PassengerType type,
                                           Config config)
    {
        BigDecimal rate = config.getDiscountRate(type);
        BigDecimal discount = baseFare.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return baseFare.subtract(discount);
    }
}
