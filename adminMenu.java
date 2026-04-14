import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class AdminMenu
{
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String CONFIG_FILE = "config.json";

    private Config config;
    private InputHelper inputHelper;

    public AdminMenu(Config config)
    {
        this.config = config;
        this.inputHelper = new InputHelper();
    }

    public void run(Scanner sc) {
        System.out.print("Enter admin password: ");
        String pwd = sc.nextLine().trim();

        if (!pwd.equals(ADMIN_PASSWORD))
        {
            System.out.println("Incorrect password. Access denied.");
            return;
        }

        System.out.println("Admin access granted.");

        boolean running = true;
        while (running)
        {
            printMenu();
            String choice = sc.nextLine().trim();

            switch (choice)
            {
                case "1": viewConfig(); break;
                case "2": updateBaseFare(sc); break;
                case "3": updateDiscount(sc); break;
                case "4": updateDailyCap(sc); break;
                case "5": updatePeakWindows(sc); break;
                case "6": running = false; break;
                default:  System.out.println("Invalid option. Please enter 1 to 6."); break;
            }
        }
    }

    private void printMenu()
    {
        System.out.println("\n==============================");
        System.out.println("       Admin Menu              ");
        System.out.println("==============================");
        System.out.println("1.View Active Configuration");
        System.out.println("2.Update Base Fare");
        System.out.println("3.Update Passenger Discount");
        System.out.println("4.Update Daily Cap");
        System.out.println("5.Update Peak Windows");
        System.out.println("6.Exit Admin Menu");
        System.out.print("Choose option (1-6): ");
    }

    private void viewConfig()
    {
        System.out.println("\n--- Active Configuration ---");
        System.out.println("\nPeak windows: " + config.getPeakWindowsDescription());

        System.out.println("\nPassenger discounts:");
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values())
        {
            BigDecimal rate = config.getDiscountRate(type);
            System.out.println("  " + type + ": "
                    + rate.multiply(new BigDecimal("100")).toPlainString() + "%");
        }

        System.out.println("\nDaily caps:");
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values())
        {
            System.out.println("  " + type + ": £" + config.getDailyCap(type));
        }

        System.out.println("\nSample base fares (Zone 1 -> Zones 1-5, Peak):");
        for (int to = 1; to <= CityRideDataset.MAX_ZONE; to++)
        {
            BigDecimal fare = config.getBaseFare(1, to, CityRideDataset.TimeBand.PEAK);
            System.out.println("  Zone 1 -> Zone " + to + ": £" + fare);
        }
    }

    private void updateBaseFare(Scanner sc)
    {
        System.out.println("\n--- Update Base Fare ---");

        int from = inputHelper.getZone(sc, "From zone (1-5): ");
        int to = inputHelper.getZone(sc, "To zone   (1-5): ");
        CityRideDataset.TimeBand band = inputHelper.getTimeBand(sc);

        System.out.println("Current fare: £" + config.getBaseFare(from, to, band));
        System.out.print("New fare (e.g. 3.50): £");
        BigDecimal newFare = inputHelper.parsePositiveDecimal(sc.nextLine().trim());

        if (newFare == null)
        {
            System.out.println("Invalid fare. Must be a positive number. No changes saved.");
            return;
        }

        config.setBaseFare(from, to, band, newFare);
        ConfigManager.saveConfig(config, CONFIG_FILE);
        System.out.println("Base fare updated and saved.");
    }

    private void updateDiscount(Scanner sc)
    {
        System.out.println("\n--- Update Passenger Discount ---");
        CityRideDataset.PassengerType type = inputHelper.getPassengerType(sc);

        System.out.println("Current: "
                + config.getDiscountRate(type).multiply(new BigDecimal("100")).toPlainString() + "%");
        System.out.print("New discount (0-100, e.g. 25 for 25%): ");
        BigDecimal percent = inputHelper.parsePositiveDecimal(sc.nextLine().trim());

        if (percent == null || percent.compareTo(BigDecimal.ZERO) < 0
                || percent.compareTo(new BigDecimal("100")) > 0)
        {
            System.out.println("Invalid discount. Must be between 0 and 100. No changes saved.");
            return;
        }

        BigDecimal rate = percent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        config.setDiscountRate(type, rate);
        ConfigManager.saveConfig(config, CONFIG_FILE);
        System.out.println("Discount updated and saved.");
    }

    private void updateDailyCap(Scanner sc)
    {
        System.out.println("\n--- Update Daily Cap ---");
        CityRideDataset.PassengerType type = inputHelper.getPassengerType(sc);

        System.out.println("Current cap: £" + config.getDailyCap(type));
        System.out.print("New daily cap (e.g. 8.00): £");
        BigDecimal newCap = inputHelper.parsePositiveDecimal(sc.nextLine().trim());

        if (newCap == null)
        {
            System.out.println("Invalid cap. Must be a positive number. No changes saved.");
            return;
        }

        config.setDailyCap(type, newCap);
        ConfigManager.saveConfig(config, CONFIG_FILE);
        System.out.println("Daily cap updated and saved.");
    }

    private void updatePeakWindows(Scanner sc)
    {
        System.out.println("\n--- Update Peak Windows ---");
        System.out.println("Current: " + config.getPeakWindowsDescription());
        System.out.print("New peak window (e.g. '07:00-09:30 and 16:00-19:00'): ");
        String description = sc.nextLine().trim();

        if (description.isEmpty())
        {
            System.out.println("Description cannot be empty. No changes saved.");
            return;
        }

        config.setPeakWindowsDescription(description);
        ConfigManager.saveConfig(config, CONFIG_FILE);
        System.out.println("Peak windows updated and saved.");
    }
}
