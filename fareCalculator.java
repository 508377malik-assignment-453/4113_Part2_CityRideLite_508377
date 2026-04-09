import java.math.BigDecimal;
import java.math.RoundingMode;

class FareCalculator {

    public static BigDecimal applyDiscount(BigDecimal baseFare,
                                           CityRideDataset.PassengerType type) {
        BigDecimal rate = CityRideDataset.DISCOUNT_RATE.get(type);
        BigDecimal discount = baseFare.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return baseFare.subtract(discount);
    }
}
