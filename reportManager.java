import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportManager
{
    public static void exportCsvReport(List<Journey> journeys, String filePath)
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath)))
        {
            pw.println("id,date,fromZone,toZone,timeBand,passengerType,baseFare,discountedFare,chargedFare");
            for (Journey j : journeys)
            {
                pw.println(j.getId() + "," + j.getDate() + "," + j.getFromZone() + ","
                        + j.getToZone() + "," + j.getTimeBand().name() + ","
                        + j.getPassengerType().name() + "," + j.getBaseFare() + ","
                        + j.getDiscountedFare() + "," + j.getChargedFare());
            }
        } catch (IOException e)
        {
            System.out.println("Error exporting CSV report: " + e.getMessage());
        }
    }

    public static void exportTextReport(SummaryData summary, List<Journey> journeys,
                                        RiderProfile profile, String filePath)
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath)))
        {
            writeSummaryHeader(pw, profile);
            writeSummaryStats(pw, summary);
            writeZonePairBreakdown(pw, summary);
            writeJourneyList(pw, journeys);
        } catch (IOException e)
        {
            System.out.println("Error exporting text report: " + e.getMessage());
        }
    }

    private static void writeSummaryHeader(PrintWriter pw, RiderProfile profile)
    {
        pw.println("==============================");
        pw.println("   CityRide Lite - End of Day Report");
        pw.println("==============================");
        pw.println("Rider:          " + profile.getName());
        pw.println("Passenger type: " + profile.getPassengerType());
        pw.println("Payment:        " + profile.getDefaultPayment());
        pw.println("Generated:      " + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        pw.println();
    }

    private static void writeSummaryStats(PrintWriter pw, SummaryData summary)
    {
        pw.println("--- Summary ---");
        pw.println("Total journeys:       " + summary.getTotalJourneys());
        pw.println("Total charged:       £" + summary.getTotalCharged().setScale(2, RoundingMode.HALF_UP));
        pw.println("Average per journey: £" + summary.getAverageFare());
        pw.println("Most expensive:       ID " + summary.getMostExpensive().getId()
                + " (£" + summary.getMostExpensive().getChargedFare() + ")");
        pw.println("Peak journeys:        " + summary.getPeakCount());
        pw.println("Off-peak journeys:    " + summary.getOffPeakCount());
        pw.println("Total savings:       £" + summary.getSavings().setScale(2, RoundingMode.HALF_UP));
        pw.println();

        pw.println("--- Cap Status ---");
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values())
        {
            BigDecimal cap = summary.getDailyCaps().get(type);
            BigDecimal running = summary.getRunningTotals().get(type);
            String reached = running != null && running.compareTo(cap) >= 0 ? "YES" : "No";
            pw.println("  " + type + ": £"
                    + (running != null ? running.setScale(2, RoundingMode.HALF_UP) : "0.00")
                    + " / £" + cap + "  Cap reached: " + reached);
        }
        pw.println();
    }

    private static void writeZonePairBreakdown(PrintWriter pw, SummaryData summary)
    {
        pw.println("--- Zone Pair Breakdown ---");
        for (Map.Entry<String, Integer> entry : summary.getZonePairCounts().entrySet())
        {
            pw.println("  " + entry.getKey() + ": " + entry.getValue() + " journey(s)");
        }
        pw.println();
    }

    private static void writeJourneyList(PrintWriter pw, List<Journey> journeys)
    {
        pw.println("--- Journey List ---");
        for (Journey j : journeys) {
            pw.println(j);
        }
    }
}
