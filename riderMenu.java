import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class RiderMenu
{
    private static final String PROFILE_FILE = "rider_profile.json";
    private static final String REPORTS_DIR  = "reports/";

    private RiderProfile activeProfile;
    private List<Journey> journeyList;
    private int nextJourneyId;
    private Config config;

    private Map<CityRideDataset.PassengerType, BigDecimal> runningTotals;

    private InputHelper inputHelper;

    public RiderMenu(Config config)
    {
        this.config = config;
        this.journeyList = new ArrayList<>();
        this.nextJourneyId = 1;
        this.inputHelper = new InputHelper();
        this.runningTotals = new HashMap<>();
        resetRunningTotals();
    }

    public void run(Scanner sc)
    {
        loadOrCreateProfile(sc);

        boolean running = true;
        while (running)
        {
            printMenu();
            String choice = sc.nextLine().trim();

            switch (choice)
            {
                case "1":  addJourney(sc); break;
                case "2":  viewAllJourneys(); break;
                case "3":  editJourney(sc); break;
                case "4":  deleteJourney(sc); break;
                case "5":  importJourneysFromCsv(sc); break;
                case "6":  exportJourneysToCsv(sc); break;
                case "7":  filterJourneys(sc); break;
                case "8":  showDailySummary(); break;
                case "9":  exportEndOfDayReport(sc); break;
                case "10": saveProfileAndJourneys(); break;
                case "11": resetDay(sc); break;
                case "12": running = offerSaveOnExit(sc); break;
                default:   System.out.println("Invalid option. Please enter 1 to 12."); break;
            }
        }
    }

    private void printMenu() {
        System.out.println("\n==============================");
        System.out.println("  Rider Menu - " + activeProfile.getName());
        System.out.println("==============================");
        System.out.println("1.Add Journey");
        System.out.println("2.View All Journeys");
        System.out.println("3.Edit Journey");
        System.out.println("4.Delete Journey");
        System.out.println("5.Import Journeys from CSV");
        System.out.println("6.Export Journeys to CSV");
        System.out.println("7.Filter Journeys");
        System.out.println("8.Daily Summary");
        System.out.println("9.Export End-of-Day Report");
        System.out.println("10.Save Profile & Journeys");
        System.out.println("11.Reset Day");
        System.out.println("12.Exit Rider Menu");
        System.out.print("Choose option: ");
    }

    private void loadOrCreateProfile(Scanner sc)
    {
        System.out.println("\n--- Rider Profile ---");
        System.out.println("1. Create new profile");
        System.out.println("2. Load existing profile");
        System.out.print("Choose (1-2): ");

        String choice = sc.nextLine().trim();

        if (choice.equals("2"))
        {
            RiderProfile loaded = ProfileManager.loadProfile(PROFILE_FILE);
            if (loaded != null)
            {
                activeProfile = loaded;
                restoreRunningTotals();
                System.out.println("Profile loaded: " + activeProfile.getName()
                        + " (" + activeProfile.getPassengerType() + ")");
                return;
            }
            System.out.println("No saved profile found. Creating a new one.");
        }

        createNewProfile(sc);
    }

    private void createNewProfile(Scanner sc)
    {
        System.out.println("\n--- Create Profile ---");
        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();

        CityRideDataset.PassengerType type = inputHelper.getPassengerType(sc);

        System.out.println("Default payment option:");
        System.out.println("1. Contactless Card");
        System.out.println("2. Mobile Pay");
        System.out.print("Choose (1-2): ");
        String payChoice = sc.nextLine().trim();
        String payment = payChoice.equals("2") ? "Mobile Pay" : "Contactless Card";

        activeProfile = new RiderProfile(name, type, payment);
        journeyList.clear();
        resetRunningTotals();
        nextJourneyId = 1;
        System.out.println("Profile created for " + name + ".");
    }

    private void saveProfileAndJourneys()
    {
        activeProfile.setJourneys(journeyList);
        ProfileManager.saveProfile(activeProfile, PROFILE_FILE);
        System.out.println("Profile and journeys saved to " + PROFILE_FILE);
    }

    private void restoreRunningTotals() {
        resetRunningTotals();
        journeyList = activeProfile.getJourneys();
        if (journeyList == null)
        {
            journeyList = new ArrayList<>();
        }
        for (Journey j : journeyList)
        {
            BigDecimal current = runningTotals.get(j.getPassengerType());
            runningTotals.put(j.getPassengerType(), current.add(j.getChargedFare()));
        }
        nextJourneyId = 1;
        for (Journey j : journeyList)
        {
            if (j.getId() >= nextJourneyId)
            {
                nextJourneyId = j.getId() + 1;
            }
        }
    }

    private void resetRunningTotals()
    {
        for (CityRideDataset.PassengerType t : CityRideDataset.PassengerType.values())
        {
            runningTotals.put(t, BigDecimal.ZERO);
        }
    }

    private void addJourney(Scanner sc) {
        System.out.println("\n--- Add Journey ---");

        String date = inputHelper.getDate(sc);
        int fromZone = inputHelper.getZone(sc, "From zone (1-5, e.g. 1): ");
        int toZone = inputHelper.getZone(sc, "To zone   (1-5, e.g. 3): ");

        CityRideDataset.PassengerType type = activeProfile.getPassengerType();
        System.out.println("Passenger type: " + type + " (from profile)");

        CityRideDataset.TimeBand band = inputHelper.getTimeBand(sc);

        Journey j = buildJourney(nextJourneyId, date, fromZone, toZone, type, band);
        journeyList.add(j);
        nextJourneyId++;

        printJourneySummary(j);
    }

    private void editJourney(Scanner sc) {
        System.out.println("\n--- Edit Journey ---");

        if (journeyList.isEmpty())
        {
            System.out.println("No journeys to edit.");
            return;
        }

        System.out.print("Enter journey ID to edit: ");
        int id = inputHelper.parseId(sc);
        if (id < 0) return;

        Journey toEdit = findJourneyById(id);
        if (toEdit == null)
        {
            System.out.println("No journey found with ID " + id + ".");
            return;
        }

        System.out.println("Current: " + toEdit);

        subtractFromRunningTotal(toEdit.getPassengerType(), toEdit.getChargedFare());

        String date = inputHelper.getDate(sc);
        int fromZone = inputHelper.getZone(sc, "From zone (1-5): ");
        int toZone = inputHelper.getZone(sc, "To zone   (1-5): ");
        CityRideDataset.TimeBand band = inputHelper.getTimeBand(sc);

        Journey updated = buildJourney(id, date, fromZone, toZone, toEdit.getPassengerType(), band);
        journeyList.set(journeyList.indexOf(toEdit), updated);

        System.out.println("Journey updated.");
        printJourneySummary(updated);
    }

    private void deleteJourney(Scanner sc) {
        System.out.println("\n--- Delete Journey ---");

        System.out.print("Enter journey ID to delete: ");
        int id = inputHelper.parseId(sc);
        if (id < 0) return;

        Journey toRemove = findJourneyById(id);
        if (toRemove == null)
        {
            System.out.println("No journey found with ID " + id + ".");
            return;
        }

        System.out.println("Journey to delete: " + toRemove);
        System.out.print("Are you sure? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes"))
        {
            subtractFromRunningTotal(toRemove.getPassengerType(), toRemove.getChargedFare());
            journeyList.remove(toRemove);
            System.out.println("Journey deleted. Running totals updated.");
        } else
        {
            System.out.println("Deletion cancelled.");
        }
    }

    private void viewAllJourneys()
    {
        System.out.println("\n--- All Journeys ---");

        if (journeyList.isEmpty()) {
            System.out.println("No journeys recorded yet.");
            return;
        }

        for (Journey j : journeyList)
        {
            System.out.println(j);
        }
    }

    private void filterJourneys(Scanner sc)
    {
        System.out.println("\n--- Filter Journeys ---");
        System.out.println("1. By Passenger Type");
        System.out.println("2. By Time Band");
        System.out.println("3. By Zone");
        System.out.println("4. By Date");
        System.out.print("Choose filter (1-4): ");

        String choice = sc.nextLine().trim();

        switch (choice) {
            case "1": filterByPassengerType(sc); break;
            case "2": filterByTimeBand(sc); break;
            case "3": filterByZone(sc); break;
            case "4": filterByDate(sc); break;
            default:  System.out.println("Invalid option."); break;
        }
    }

    private void filterByPassengerType(Scanner sc)
    {
        CityRideDataset.PassengerType type = inputHelper.getPassengerType(sc);
        List<Journey> results = new ArrayList<>();

        for (Journey j : journeyList)
        {
            if (j.getPassengerType() == type) results.add(j);
        }

        printFilterResults(results, "passenger type: " + type);
    }

    private void filterByTimeBand(Scanner sc)
    {
        CityRideDataset.TimeBand band = inputHelper.getTimeBand(sc);
        List<Journey> results = new ArrayList<>();

        for (Journey j : journeyList)
        {
            if (j.getTimeBand() == band) results.add(j);
        }

        String label = band == CityRideDataset.TimeBand.PEAK ? "Peak" : "Off-peak";
        printFilterResults(results, "time band: " + label);
    }

    private void filterByZone(Scanner sc)
    {
        int zone = inputHelper.getZone(sc, "Enter zone to filter by (1-5, e.g. 2): ");
        List<Journey> results = new ArrayList<>();

        for (Journey j : journeyList)
        {
            if (j.getFromZone() == zone || j.getToZone() == zone) results.add(j);
        }

        printFilterResults(results, "zone: " + zone);
    }

    private void filterByDate(Scanner sc)
    {
        String date = inputHelper.getDate(sc);
        List<Journey> results = new ArrayList<>();

        for (Journey j : journeyList) {
            if (j.getDate().equalsIgnoreCase(date)) results.add(j);
        }

        printFilterResults(results, "date: " + date);
    }

    private void printFilterResults(List<Journey> results, String filterDesc)
    {
        System.out.println("\nJourneys matching " + filterDesc + ":");

        if (results.isEmpty())
        {
            System.out.println("  No journeys found.");
        } else
        {
            for (Journey j : results) System.out.println(j);
        }
    }

    private void showDailySummary() {
        System.out.println("\n--- Daily Summary ---");

        if (journeyList.isEmpty())
        {
            System.out.println("No journeys recorded yet.");
            return;
        }

        SummaryData summary = calculateSummary();
        printSummaryToConsole(summary);
    }

    private SummaryData calculateSummary()
    {
        BigDecimal totalCharged = BigDecimal.ZERO;
        BigDecimal totalBase = BigDecimal.ZERO;
        Journey mostExpensive = journeyList.get(0);
        int peakCount = 0;
        int offPeakCount = 0;
        Map<String, Integer> zonePairCounts = new HashMap<>();

        for (Journey j : journeyList)
        {
            totalCharged = totalCharged.add(j.getChargedFare());
            totalBase = totalBase.add(j.getBaseFare());

            if (j.getChargedFare().compareTo(mostExpensive.getChargedFare()) > 0)
            {
                mostExpensive = j;
            }

            if (j.getTimeBand() == CityRideDataset.TimeBand.PEAK)
            {
                peakCount++;
            } else {
                offPeakCount++;
            }

            String pair = "Zone " + j.getFromZone() + "->" + j.getToZone();
            zonePairCounts.put(pair, zonePairCounts.getOrDefault(pair, 0) + 1);
        }

        BigDecimal averageFare = totalCharged.divide(
                new BigDecimal(journeyList.size()), 2, RoundingMode.HALF_UP);

        BigDecimal savings = totalBase.subtract(totalCharged);

        return new SummaryData(journeyList.size(), totalCharged, averageFare,
                mostExpensive, peakCount, offPeakCount, savings, zonePairCounts,
                runningTotals, config.getDailyCaps());
    }

    private void printSummaryToConsole(SummaryData s)
    {
        System.out.println("Total journeys:        " + s.getTotalJourneys());
        System.out.println("Total charged:        £" + s.getTotalCharged().setScale(2, RoundingMode.HALF_UP));
        System.out.println("Average per journey:  £" + s.getAverageFare());
        System.out.println("Most expensive:        ID " + s.getMostExpensive().getId()
                + " (£" + s.getMostExpensive().getChargedFare() + ")");
        System.out.println("Peak journeys:         " + s.getPeakCount());
        System.out.println("Off-peak journeys:     " + s.getOffPeakCount());
        System.out.println("Total savings (cap):  £" + s.getSavings().setScale(2, RoundingMode.HALF_UP));

        System.out.println("\nJourneys by zone pair:");
        for (Map.Entry<String, Integer> entry : s.getZonePairCounts().entrySet())
        {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\nCap status by passenger type:");
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values())
        {
            BigDecimal cap = s.getDailyCaps().get(type);
            BigDecimal running = s.getRunningTotals().get(type);
            String reached = running != null && running.compareTo(cap) >= 0 ? "YES" : "No";
            System.out.println("  " + type + ": £"
                    + (running != null ? running.setScale(2, RoundingMode.HALF_UP) : "0.00")
                    + " / £" + cap + "  Cap reached: " + reached);
        }
    }

    private void importJourneysFromCsv(Scanner sc) {
        System.out.print("Enter CSV file path to import (e.g. journeys.csv): ");
        String filePath = sc.nextLine().trim();

        List<Journey> imported = CSVmanager.importJourneys(filePath);

        if (imported.isEmpty()) {
            System.out.println("No journeys imported.");
            return;
        }

        for (Journey j : imported) {
            j.setId(nextJourneyId);
            nextJourneyId++;

            BigDecimal cappedFare = applyDailyCap(j.getPassengerType(), j.getDiscountedFare());
            j.setChargedFare(cappedFare);
            journeyList.add(j);
        }

        System.out.println(imported.size() + " journeys imported successfully.");
    }

    private void exportJourneysToCsv(Scanner sc)
    {
        System.out.print("Enter filename to export to (e.g. journeys.csv): ");
        String filePath = sc.nextLine().trim();
        CSVmanager.exportJourneys(journeyList, filePath);
        System.out.println("Journeys exported to " + filePath);
    }

    private void exportEndOfDayReport(Scanner sc)
    {
        if (journeyList.isEmpty())
        {
            System.out.println("No journeys to export.");
            return;
        }

        SummaryData summary = calculateSummary();
        String riderName  = activeProfile.getName().replaceAll("\\s+", "_");
        String timestamp  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));

        new File(REPORTS_DIR).mkdirs();

        String csvPath  = REPORTS_DIR + "report_" + riderName + "_" + timestamp + ".csv";
        String textPath = REPORTS_DIR + "report_" + riderName + "_" + timestamp + ".txt";

        ReportManager.exportCsvReport(journeyList, csvPath);
        ReportManager.exportTextReport(summary, journeyList, activeProfile, textPath);

        System.out.println("CSV report saved:  " + csvPath);
        System.out.println("Text report saved: " + textPath);
    }

    private void resetDay(Scanner sc) {
        System.out.print("This will clear ALL journeys and reset totals. Continue? (yes/no): ");
        String confirm = sc.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes"))
        {
            journeyList.clear();
            nextJourneyId = 1;
            resetRunningTotals();
            System.out.println("Day reset. All journeys cleared.");
        } else
        {
            System.out.println("Reset cancelled.");
        }
    }

    private boolean offerSaveOnExit(Scanner sc)
    {
        System.out.print("Save profile and journeys before exiting? (yes/no): ");
        String answer = sc.nextLine().trim();

        if (answer.equalsIgnoreCase("yes"))
        {
            saveProfileAndJourneys();
        }

        System.out.println("Returning to main menu.");
        return false;
    }

    private Journey buildJourney(int id, String date, int fromZone, int toZone,
                                 CityRideDataset.PassengerType type,
                                 CityRideDataset.TimeBand band)
    {
        int zonesCrossed = Math.abs(toZone - fromZone) + 1;
        BigDecimal baseFare = config.getBaseFare(fromZone, toZone, band);
        BigDecimal discounted = FareCalculator.applyDiscount(baseFare, type, config);
        BigDecimal charged = applyDailyCap(type, discounted);

        return new Journey(id, date, fromZone, toZone, type, band,
                zonesCrossed, baseFare, discounted, charged);
    }

    private BigDecimal applyDailyCap(CityRideDataset.PassengerType type, BigDecimal fare)
    {
        BigDecimal cap = config.getDailyCap(type);
        BigDecimal running = runningTotals.get(type);

        if (running.compareTo(cap) >= 0)
        {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal remaining = cap.subtract(running);

        if (fare.compareTo(remaining) > 0)
        {
            runningTotals.put(type, cap);
            return remaining.setScale(2, RoundingMode.HALF_UP);
        }

        runningTotals.put(type, running.add(fare));
        return fare.setScale(2, RoundingMode.HALF_UP);
    }

    private void subtractFromRunningTotal(CityRideDataset.PassengerType type, BigDecimal amount)
    {
        BigDecimal newTotal = runningTotals.get(type).subtract(amount);
        if (newTotal.compareTo(BigDecimal.ZERO) < 0) newTotal = BigDecimal.ZERO;
        runningTotals.put(type, newTotal);
    }

    private Journey findJourneyById(int id)
    {
        for (Journey j : journeyList)
        {
            if (j.getId() == id) return j;
        }
        return null;
    }

    private void printJourneySummary(Journey j)
    {
        System.out.println("\nJourney added successfully!");
        System.out.println("Journey ID:    " + j.getId());
        System.out.println("Route:         Zone " + j.getFromZone() + " -> Zone " + j.getToZone());
        System.out.println("Zones crossed: " + j.getZonesCrossed());
        System.out.println("Base fare:    £" + j.getBaseFare());
        System.out.println("Discount:     £" + j.getBaseFare().subtract(j.getDiscountedFare()).setScale(2, RoundingMode.HALF_UP));
        System.out.println("Charged fare: £" + j.getChargedFare());
    }
}
