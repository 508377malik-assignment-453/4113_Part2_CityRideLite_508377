import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

class AdminSession
{
    private static final String ADMIN_PASSWORD = "admin123";

    private static final String CONFIG_FILE = "config.json";

    public boolean login(Scanner sc)
    {
        System.out.println("\n--- Admin Login ---");
        int attemptsLeft = 3;

        while (attemptsLeft > 0) {
            System.out.print("Enter admin password: ");
            String entered = sc.nextLine().trim();

            if (entered.equals(ADMIN_PASSWORD))
            {
                System.out.println("  Access granted.");
                return true;
            }

            attemptsLeft--;
            System.out.println("  Incorrect password. " + attemptsLeft + " attempt(s) remaining.");
        }

        System.out.println("  Too many failed attempts. Access denied.");
        return false;
    }

    public void runAdminMenu(Scanner sc)
    {

        if (!login(sc))
        {
            return;
        }

        boolean running = true;

        while (running)
        {
            printAdminMenu();
            String choice = sc.nextLine().trim();

            switch (choice)
            {
                case "1":  viewConfig(); break;
                case "2":  updateBaseFare(sc); break;
                case "3":  updateDiscountRate(sc); break;
                case "4":  updateDailyCap(sc); break;
                case "5":  saveConfig(); break;
                case "0":  running = false; break;
                default:
                    System.out.println("  Invalid option. Please enter 0-5.");
            }
        }

        System.out.println("  Admin session ended.");
    }

    private void printAdminMenu()
    {
        System.out.println("\n==============================");
        System.out.println("  CityRide Lite - Admin Menu");
        System.out.println("==============================");
        System.out.println("1. View Active Config");
        System.out.println("2. Update Base Fare");
        System.out.println("3. Update Discount Rate");
        System.out.println("4. Update Daily Cap");
        System.out.println("5. Save Config to File");
        System.out.println("0. Back to Main Menu");
        System.out.print("Choose option: ");
    }

    private void viewConfig()
    {
        System.out.println("\n--- Active Configuration ---");

        System.out.println("\n  Discount Rates:");
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values())
        {
            BigDecimal rate    = CityRideDataset.DISCOUNT_RATE.get(type);
            BigDecimal percent = rate.multiply(new BigDecimal("100"));
            System.out.println("    " + type + " : " + percent + "%");
        }

        System.out.println("\n  Daily Caps:");
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values())
        {
            System.out.println("    " + type + " : £" + CityRideDataset.DAILY_CAP.get(type));
        }

        System.out.println("\n  Base Fares (Peak / Off-Peak):");
        for (int from = CityRideDataset.MIN_ZONE; from <= CityRideDataset.MAX_ZONE; from++)
        {
            for (int to = CityRideDataset.MIN_ZONE; to <= CityRideDataset.MAX_ZONE; to++)
            {
                BigDecimal peak    = CityRideDataset.getBaseFare(from, to, CityRideDataset.TimeBand.PEAK);
                BigDecimal offPeak = CityRideDataset.getBaseFare(from, to, CityRideDataset.TimeBand.OFF_PEAK);
                System.out.println("    Zone " + from + " -> Zone " + to
                        + "  |  Peak: £" + peak
                        + "  |  Off-Peak: £" + offPeak);
            }
        }
    }

    private void updateBaseFare(Scanner sc)
    {
        System.out.println("\n--- Update Base Fare ---");

        int fromZone = InputValidator.readZone(sc, "From zone (1-5): ");
        int toZone   = InputValidator.readZone(sc, "To zone   (1-5): ");
        CityRideDataset.TimeBand band = InputValidator.readTimeBand(sc);

        BigDecimal currentFare = CityRideDataset.getBaseFare(fromZone, toZone, band);
        System.out.println("  Current fare: £" + currentFare);

        BigDecimal newFare = InputValidator.readPositiveDecimal(sc,
                "Enter new fare (e.g. 3.50): £");

        BigDecimal maxFare = new BigDecimal("50.00");
        if (newFare.compareTo(maxFare) > 0)
        {
            System.out.println("  Validation failed: fare cannot exceed £" + maxFare + ".");
            return;
        }

        String key = CityRideDataset.fareKey(fromZone, toZone, band);
        CityRideDataset.BASE_FARE.put(key, CityRideDataset.money(newFare.toString()));
        System.out.println("  Fare updated: Zone " + fromZone + " -> Zone " + toZone
                + " (" + band + ") = £" + newFare);
        System.out.println("  Remember to save config (option 5) to persist changes.");
    }

    private void updateDiscountRate(Scanner sc)
    {
        System.out.println("\n--- Update Discount Rate ---");

        CityRideDataset.PassengerType type = InputValidator.readPassengerType(sc);
        BigDecimal currentRate = CityRideDataset.DISCOUNT_RATE.get(type);
        System.out.println("  Current rate: " + currentRate.multiply(new BigDecimal("100")) + "%");

        BigDecimal newRate = InputValidator.readDiscountRate(sc);

        CityRideDataset.DISCOUNT_RATE.put(type, CityRideDataset.money(newRate.toString()));
        System.out.println("  Discount updated: " + type + " = "
                + newRate.multiply(new BigDecimal("100")) + "%");
        System.out.println("  Remember to save config (option 5) to persist changes.");
    }

    private void updateDailyCap(Scanner sc)
    {
        System.out.println("\n--- Update Daily Cap ---");

        CityRideDataset.PassengerType type = InputValidator.readPassengerType(sc);
        System.out.println("  Current cap: £" + CityRideDataset.DAILY_CAP.get(type));

        BigDecimal newCap = InputValidator.readPositiveDecimal(sc,
                "Enter new daily cap (e.g. 9.00): £");

        BigDecimal maxCap = new BigDecimal("100.00");
        if (newCap.compareTo(maxCap) > 0) {
            System.out.println("  Validation failed: cap cannot exceed £" + maxCap + ".");
            return;
        }

        CityRideDataset.DAILY_CAP.put(type, CityRideDataset.money(newCap.toString()));
        System.out.println("  Daily cap updated: " + type + " = £" + newCap);
        System.out.println("  Remember to save config (option 5) to persist changes.");
    }

    private void saveConfig()
    {
        try
        {
            JsonHandler.saveConfig(CONFIG_FILE);
            System.out.println("  Configuration saved to " + CONFIG_FILE + ".");
        } catch (IOException e)
        {
            System.out.println("  Error saving config: " + e.getMessage());
        }
    }
}

