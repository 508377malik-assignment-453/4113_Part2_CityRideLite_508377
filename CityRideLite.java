import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CityRideLite {

    private ArrayList<Journey> journeyList;
    private int nextId;

    private BigDecimal adultTotal = BigDecimal.ZERO;
    private BigDecimal studentTotal = BigDecimal.ZERO;
    private BigDecimal childTotal = BigDecimal.ZERO;
    private BigDecimal seniorTotal = BigDecimal.ZERO;

    public CityRideLite()
    {
        journeyList = new ArrayList<>();
        nextId = 1;
    }

    public static void main(String[] args)
    {
        CityRideLite app = new CityRideLite();
        app.start();
    }

    private void start() {

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("Welcome to CityRide Lite!");

        while (running) {
            System.out.println("\n===============================");
            System.out.println("        CityRide Lite Menu       ");
            System.out.println("==================================");
            System.out.println("1. Add Journey");
            System.out.println("2. View All Journeys");
            System.out.println("3. Filter Journeys");
            System.out.println("4. Daily Summary");
            System.out.println("5. Totals by Passenger Type");
            System.out.println("6. Remove Journey");
            System.out.println("7. Reset Day");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                addJourney(sc);
            } else if (choice.equals("2")) {
                viewAllJourneys();
            } else if (choice.equals("3")) {
                filterJourneys(sc);
            } else if (choice.equals("4")) {
                showDailySummary();
            } else if (choice.equals("5")) {
                showTotalsByType();
            } else if (choice.equals("6")) {
                removeJourney(sc);
            } else if (choice.equals("7")) {
                resetDay(sc);
            } else if (choice.equals("8")) {
                running = false;
                System.out.println("Thank you for using CityRide Lite. Goodbye!");
            } else {
                System.out.println("Invalid option. Please enter a number from 1 to 8.");
            }
        }

        sc.close();
    }

    private void addJourney(Scanner sc) {

        System.out.println("\n--- Add Journey ---");

        String date = getDate(sc);

        int fromZone = getZone(sc, "From zone (1-5): ");
        int toZone = getZone(sc, "To zone (1-5): ");

        CityRideDataset.PassengerType type = getPassengerType(sc);
        CityRideDataset.TimeBand band = getTimeBand(sc);

        int zonesCrossed = Math.abs(toZone - fromZone) + 1;

        BigDecimal baseFare = CityRideDataset.getBaseFare(fromZone, toZone, band);
        BigDecimal discountedFare = FareCalculator.applyDiscount(baseFare, type);
        BigDecimal chargedFare = applyCap(type, discountedFare);

        Journey j = new Journey(nextId, date, fromZone, toZone, type, band, zonesCrossed, baseFare, discountedFare, chargedFare);
        journeyList.add(j);
        nextId++;

        System.out.println("\nJourney added successfully!");
        System.out.println("Journey ID: " + j.getId());
        System.out.println("Route: Zone " + fromZone + " -> Zone " + toZone);
        System.out.println("Zones Crossed: " + zonesCrossed);
        System.out.println("Base Fare: £" + baseFare);
        System.out.println("Discount: £" + baseFare.subtract(discountedFare));
        System.out.println("Charged Fare: £" + chargedFare);
    }

    private void viewAllJourneys()
    {
        System.out.println("\n--- All Journeys ---");

        if (journeyList.isEmpty())
        {
            System.out.println("No journeys recorded yet.");
            return;
        }

        for (Journey j : journeyList) {
            System.out.println(j);
        }
    }

    private void filterJourneys(Scanner sc) {
        System.out.println("\n--- Filter Journeys ---");
        System.out.println("1. By Passenger Type");
        System.out.println("2. By Time Band");
        System.out.println("3. By Zone");
        System.out.println("4. By Date");
        System.out.print("Choose filter: ");

        String choice = sc.nextLine().trim();

        if (choice.equals("1"))
        {
            filterByPassengerType(sc);
        } else if (choice.equals("2"))
        {
            filterByTimeBand(sc);
        } else if (choice.equals("3"))
        {
            filterByZone(sc);
        } else if (choice.equals("4"))
        {
            filterByDate(sc);
        } else
        {
            System.out.println("Invalid option.");
        }
    }

    private void filterByPassengerType(Scanner sc)
    {

        CityRideDataset.PassengerType type = getPassengerType(sc);
        ArrayList<Journey> results = new ArrayList<>();

        for (Journey j : journeyList)
        {
            if (j.getPassengerType() == type)
            {
                results.add(j);
            }
        }

        printFilterResults(results, "passenger type " + getTypeLabel(type));
    }

    private void filterByTimeBand(Scanner sc)
    {

        CityRideDataset.TimeBand band = getTimeBand(sc);
        ArrayList<Journey> results = new ArrayList<>();

        for (Journey j : journeyList)
        {
            if (j.getTimeBand() == band)
            {
                results.add(j);
            }
        }

        String label = band == CityRideDataset.TimeBand.PEAK ? "Peak" : "Off-peak";
        printFilterResults(results, "time band " + label);
    }

    private void filterByZone(Scanner sc)
    {
        int zone = getZone(sc, "Enter zone to filter by (1-5): ");
        ArrayList<Journey> results = new ArrayList<>();

        for (Journey j : journeyList)
        {
            if (j.getFromZone() == zone || j.getToZone() == zone)
            {
                results.add(j);
            }
        }

        printFilterResults(results, "zone " + zone);
    }

    private void filterByDate(Scanner sc)
    {
        String date = getDate(sc);
        ArrayList<Journey> results = new ArrayList<>();

        for (Journey j : journeyList)
        {
            if (j.getDate().equalsIgnoreCase(date))
            {
                results.add(j);
            }
        }

        printFilterResults(results, "date " + date);
    }

    private void printFilterResults(ArrayList<Journey> results, String filterDesc)
    {
        System.out.println("\nJourneys matching " + filterDesc + ":");

        if (results.isEmpty())
        {
            System.out.println("  No journeys found.");
        } else
        {
            for (Journey j : results)
            {
                System.out.println(j);
            }
        }
    }

    private void showDailySummary()
    {

        System.out.println("\n--- Daily Summary ---");

        if (journeyList.isEmpty())
        {
            System.out.println("No journeys recorded yet.");
            return;
        }

        BigDecimal totalCharged = BigDecimal.ZERO;
        Journey mostExpensive = journeyList.get(0);
        int peakCount = 0;
        int offPeakCount = 0;

        for (Journey j : journeyList)
        {
            totalCharged = totalCharged.add(j.getChargedFare());

            if (j.getChargedFare().compareTo(mostExpensive.getChargedFare()) > 0)
            {
                mostExpensive = j;
            }
            if (j.getTimeBand() == CityRideDataset.TimeBand.PEAK)
            {
                peakCount++;
            } else
            {
                offPeakCount++;
            }
        }

        BigDecimal averageFare = totalCharged.divide(
                new BigDecimal(journeyList.size()), 2, RoundingMode.HALF_UP);

        System.out.println("Total journeys: " + journeyList.size());
        System.out.println("Total charged: £" + totalCharged.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Average cost per journey: £" + averageFare);
        System.out.println("Most expensive journey: ID " + mostExpensive.getId() + " (£" + mostExpensive.getChargedFare() + ")");
        System.out.println("Peak journey: " + peakCount);
        System.out.println("Off-peak journeys: " + offPeakCount);
    }

    private void showTotalsByType() {
        System.out.println("\n--- Totals by Passenger Type ---");

        CityRideDataset.PassengerType[] types = CityRideDataset.PassengerType.values();

        for (CityRideDataset.PassengerType type : types) {

            int count = 0;
            BigDecimal preDiscount = BigDecimal.ZERO;
            BigDecimal afterDiscount = BigDecimal.ZERO;
            BigDecimal charged = BigDecimal.ZERO;

            for (Journey j : journeyList) {
                if (j.getPassengerType() == type)
                {
                    count++;
                    preDiscount = preDiscount.add(j.getBaseFare());
                    afterDiscount = afterDiscount.add(j.getDiscountedFare());
                    charged = charged.add(j.getChargedFare());
                }
            }

            BigDecimal cap = CityRideDataset.DAILY_CAP.get(type);
            String capReached = getRunningTotal(type).compareTo(cap) >= 0 ? "Yes" : "No";

            System.out.println("\n  " + getTypeLabel(type));
            System.out.println("Journeys: " + count);
            System.out.println("Pre-discount: £" + preDiscount.setScale(2, RoundingMode.HALF_UP));
            System.out.println("After discount: £" + afterDiscount.setScale(2, RoundingMode.HALF_UP));
            System.out.println("Charged total: £" + charged.setScale(2, RoundingMode.HALF_UP) + "  (cap: £" + cap + ")");
            System.out.println("Cap reached?: " + capReached);
        }
    }

    private void removeJourney(Scanner sc) {

        System.out.println("\n--- Remove Journey ---");
        System.out.print("Enter journey ID to remove: ");

        int id;

        try
        {
            id = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a numeric ID.");
            return;
        }

        Journey toRemove = null;

        for (Journey j : journeyList)
        {
            if (j.getId() == id)
            {
                toRemove = j;
                break;
            }
        }

        if (toRemove == null)
        {
            System.out.println("No journey found with ID " + id + ".");
            return;
        }

        System.out.println("Journey to remove: " + toRemove);
        System.out.print("Are you sure? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes"))
        {
            BigDecimal newTotal = getRunningTotal(toRemove.getPassengerType()).subtract(toRemove.getChargedFare());
            setRunningTotal(toRemove.getPassengerType(), newTotal);

            journeyList.remove(toRemove);
            System.out.println("Journey removed. Totals updated.");
        } else
        {
            System.out.println("Removal cancelled.");
        }
    }

    private void resetDay(Scanner sc)
    {
        System.out.println("\n--- Reset Day ---");
        System.out.print("This will clear ALL journeys and reset totals. Continue? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes"))
        {
            journeyList.clear();
            nextId = 1;
            adultTotal = BigDecimal.ZERO;
            studentTotal = BigDecimal.ZERO;
            childTotal = BigDecimal.ZERO;
            seniorTotal = BigDecimal.ZERO;
            System.out.println("Day reset. All journeys cleared.");
        } else {
            System.out.println("Reset cancelled.");
        }
    }

    private BigDecimal applyCap(CityRideDataset.PassengerType type, BigDecimal fare)
    {

        BigDecimal cap = CityRideDataset.DAILY_CAP.get(type);
        BigDecimal running = getRunningTotal(type);

        if (running.compareTo(cap) >= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal remaining = cap.subtract(running);

        if (fare.compareTo(remaining) > 0) {
            setRunningTotal(type, cap);
            return remaining.setScale(2, RoundingMode.HALF_UP);
        }

        setRunningTotal(type, running.add(fare));
        return fare.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getRunningTotal(CityRideDataset.PassengerType type)
    {

        if (type == CityRideDataset.PassengerType.ADULT)
            return adultTotal;
        if (type == CityRideDataset.PassengerType.STUDENT)
            return studentTotal;
        if (type == CityRideDataset.PassengerType.CHILD)
            return childTotal;
        else
        {
            return seniorTotal;
        }
    }

    private void setRunningTotal(CityRideDataset.PassengerType type, BigDecimal value)
    {
        if (type == CityRideDataset.PassengerType.ADULT)
            adultTotal = value;
        else if (type == CityRideDataset.PassengerType.STUDENT)
            studentTotal = value;
        else if (type == CityRideDataset.PassengerType.CHILD)
            childTotal = value;
        else
            seniorTotal = value;
    }

    private String getDate(Scanner sc)
    {

        while (true) {
            System.out.print("Enter date (dd/MM/yyyy): ");
            String input = sc.nextLine().trim();

            if (input.matches("\\d{2}/\\d{2}/\\d{4}"))
            {
                return input;
            }

            System.out.println("Invalid date. Please use dd/MM/yyyy, e.g. 10/03/2026.");
        }
    }

    private int getZone(Scanner sc, String message)
    {

        while (true) {

            System.out.print(message);

            try
            {
                int zone = Integer.parseInt(sc.nextLine().trim());

                if (zone >= CityRideDataset.MIN_ZONE && zone <= CityRideDataset.MAX_ZONE)
                {
                    return zone;
                }

                System.out.println("Zone must be between 1 and 5.");

            } catch (NumberFormatException e)
            {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private CityRideDataset.PassengerType getPassengerType(Scanner sc)
    {

        while (true) {

            System.out.println("Passenger type:");
            System.out.println("1. Adult");
            System.out.println("2. Student");
            System.out.println("3. Child");
            System.out.println("4. Senior Citizen");
            System.out.print("Choose (1-4): ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1"))
                return CityRideDataset.PassengerType.ADULT;
            if (choice.equals("2"))
                return CityRideDataset.PassengerType.STUDENT;
            if (choice.equals("3"))
                return CityRideDataset.PassengerType.CHILD;
            if (choice.equals("4"))
                return CityRideDataset.PassengerType.SENIOR_CITIZEN;

            System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
        }
    }

    private CityRideDataset.TimeBand getTimeBand(Scanner sc)
    {

        while (true) {

            System.out.print("Time band (1 = Peak, 2 = Off-peak): ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) return CityRideDataset.TimeBand.PEAK;
            if (choice.equals("2")) return CityRideDataset.TimeBand.OFF_PEAK;

            System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }

    private String getTypeLabel(CityRideDataset.PassengerType type)
    {

        if (type == CityRideDataset.PassengerType.STUDENT)
            return "Student";
        if (type == CityRideDataset.PassengerType.CHILD)
            return "Child";
        if (type == CityRideDataset.PassengerType.SENIOR_CITIZEN)
            return "Senior Citizen";
        else {
            return "Adult";
        }
    }

    class Journey
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

        public Journey(int id, String date, int fromZone, int toZone, CityRideDataset.PassengerType passengerType, CityRideDataset.TimeBand timeBand, int zonesCrossed, BigDecimal baseFare, BigDecimal discountedFare,
                       BigDecimal chargedFare)
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

        public int getId()
        {
            return id;
        }

        public String getDate()
        {
            return date;
        }

        public int getFromZone()
        {
            return fromZone;
        }

        public int getToZone()
        {
            return toZone;
        }

        public CityRideDataset.PassengerType getPassengerType()
        {
            return passengerType;
        }

        public CityRideDataset.TimeBand getTimeBand()
        {
            return timeBand;
        }

        public int getZonesCrossed()
        {
            return zonesCrossed;
        }

        public BigDecimal getBaseFare()
        {
            return baseFare;
        }

        public BigDecimal getDiscountedFare()
        {
            return discountedFare;
        }

        public BigDecimal getChargedFare()
        {
            return chargedFare;
        }

        public String toString()
        {

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

    class FareCalculator
    {

        public static BigDecimal applyDiscount(BigDecimal baseFare, CityRideDataset.PassengerType type)
        {
            BigDecimal rate = CityRideDataset.DISCOUNT_RATE.get(type);
            BigDecimal discount = baseFare.multiply(rate).setScale(2, RoundingMode.HALF_UP);

            return baseFare.subtract(discount);
        }
    }

    final class CityRideDataset
    {

        private CityRideDataset() {
        }

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

        public static final Map<PassengerType, BigDecimal> DISCOUNT_RATE = Map.of(
                PassengerType.ADULT, new BigDecimal("0.00"),
                PassengerType.STUDENT, new BigDecimal("0.25"),
                PassengerType.CHILD, new BigDecimal("0.50"),
                PassengerType.SENIOR_CITIZEN, new BigDecimal("0.30")
        );

        public static final Map<PassengerType, BigDecimal> DAILY_CAP = Map.of(
                PassengerType.ADULT, new BigDecimal("8.00"),
                PassengerType.STUDENT, new BigDecimal("6.00"),
                PassengerType.CHILD, new BigDecimal("4.00"),
                PassengerType.SENIOR_CITIZEN, new BigDecimal("7.00")
        );

        public static final Map<String, BigDecimal> BASE_FARE = buildBaseFare();

        public static BigDecimal getBaseFare(int fromZone, int toZone, TimeBand timeBand)
        {
            return BASE_FARE.get(key(fromZone, toZone, timeBand));
        }

        public static String key(int fromZone, int toZone, TimeBand timeBand)
        {
            return fromZone + "-" + toZone + "-" + timeBand.name();
        }

        private static BigDecimal money(String amount) {
            return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
        }

        private static Map<String, BigDecimal> buildBaseFare()
        {

            Map<String, BigDecimal> m = new HashMap<>();

            put(m, 1, 1, TimeBand.PEAK, "2.50");
            put(m, 1, 2, TimeBand.PEAK, "3.20");
            put(m, 1, 3, TimeBand.PEAK, "3.80");
            put(m, 1, 4, TimeBand.PEAK, "4.40");
            put(m, 1, 5, TimeBand.PEAK, "5.00");

            put(m, 2, 1, TimeBand.PEAK, "3.20");
            put(m, 2, 2, TimeBand.PEAK, "2.30");
            put(m, 2, 3, TimeBand.PEAK, "3.10");
            put(m, 2, 4, TimeBand.PEAK, "3.80");
            put(m, 2, 5, TimeBand.PEAK, "4.50");

            put(m, 3, 1, TimeBand.PEAK, "3.80");
            put(m, 3, 2, TimeBand.PEAK, "3.10");
            put(m, 3, 3, TimeBand.PEAK, "2.10");
            put(m, 3, 4, TimeBand.PEAK, "3.00");
            put(m, 3, 5, TimeBand.PEAK, "3.70");

            put(m, 4, 1, TimeBand.PEAK, "4.40");
            put(m, 4, 2, TimeBand.PEAK, "3.80");
            put(m, 4, 3, TimeBand.PEAK, "3.00");
            put(m, 4, 4, TimeBand.PEAK, "2.00");
            put(m, 4, 5, TimeBand.PEAK, "2.90");

            put(m, 5, 1, TimeBand.PEAK, "5.00");
            put(m, 5, 2, TimeBand.PEAK, "4.50");
            put(m, 5, 3, TimeBand.PEAK, "3.70");
            put(m, 5, 4, TimeBand.PEAK, "2.90");
            put(m, 5, 5, TimeBand.PEAK, "1.90");

            put(m, 1, 1, TimeBand.OFF_PEAK, "2.00");
            put(m, 1, 2, TimeBand.OFF_PEAK, "2.70");
            put(m, 1, 3, TimeBand.OFF_PEAK, "3.20");
            put(m, 1, 4, TimeBand.OFF_PEAK, "3.70");
            put(m, 1, 5, TimeBand.OFF_PEAK, "4.20");

            put(m, 2, 1, TimeBand.OFF_PEAK, "2.70");
            put(m, 2, 2, TimeBand.OFF_PEAK, "1.90");
            put(m, 2, 3, TimeBand.OFF_PEAK, "2.60");
            put(m, 2, 4, TimeBand.OFF_PEAK, "3.20");
            put(m, 2, 5, TimeBand.OFF_PEAK, "3.80");

            put(m, 3, 1, TimeBand.OFF_PEAK, "3.20");
            put(m, 3, 2, TimeBand.OFF_PEAK, "2.60");
            put(m, 3, 3, TimeBand.OFF_PEAK, "1.70");
            put(m, 3, 4, TimeBand.OFF_PEAK, "2.50");
            put(m, 3, 5, TimeBand.OFF_PEAK, "3.10");

            put(m, 4, 1, TimeBand.OFF_PEAK, "3.70");
            put(m, 4, 2, TimeBand.OFF_PEAK, "3.20");
            put(m, 4, 3, TimeBand.OFF_PEAK, "2.50");
            put(m, 4, 4, TimeBand.OFF_PEAK, "1.60");
            put(m, 4, 5, TimeBand.OFF_PEAK, "2.40");

            put(m, 5, 1, TimeBand.OFF_PEAK, "4.20");
            put(m, 5, 2, TimeBand.OFF_PEAK, "3.80");
            put(m, 5, 3, TimeBand.OFF_PEAK, "3.10");
            put(m, 5, 4, TimeBand.OFF_PEAK, "2.40");
            put(m, 5, 5, TimeBand.OFF_PEAK, "1.50");

            return Map.copyOf(m);
        }

        private static void put(Map<String, BigDecimal> m, int from, int to, TimeBand band, String amount)
        {
            m.put(key(from, to, band), money(amount));
        }
    }
}