import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CSVmanager
{
    private static final String CSV_HEADER =
            "id,date,fromZone,toZone,timeBand,passengerType,zonesCrossed,baseFare,discountedFare,chargedFare";

    public static void exportJourneys(List<Journey> journeys, String filePath)
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath)))
        {
            pw.println(CSV_HEADER);
            for (Journey j : journeys)
            {
                pw.println(buildCsvLine(j));
            }
        } catch (IOException e)
        {
            System.out.println("Error exporting CSV: " + e.getMessage());
        }
    }

    public static List<Journey> importJourneys(String filePath)
    {
        List<Journey> journeys = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists())
        {
            System.out.println("File not found: " + filePath);
            return journeys;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();

            String line;
            while ((line = br.readLine()) != null)
            {
                Journey j = parseCsvLine(line);
                if (j != null)
                {
                    journeys.add(j);
                }
            }
        } catch (IOException e) {
            System.out.println("Error importing CSV: " + e.getMessage());
        }

        return journeys;
    }

    private static String buildCsvLine(Journey j)
    {
        return j.getId() + ","
                + j.getDate() + ","
                + j.getFromZone() + ","
                + j.getToZone() + ","
                + j.getTimeBand().name() + ","
                + j.getPassengerType().name() + ","
                + j.getZonesCrossed() + ","
                + j.getBaseFare() + ","
                + j.getDiscountedFare() + ","
                + j.getChargedFare();
    }

    private static Journey parseCsvLine(String line)
    {
        try
        {
            String[] parts = line.split(",");
            if (parts.length < 10) return null;

            int id = Integer.parseInt(parts[0].trim());
            String date = parts[1].trim();
            int fromZone = Integer.parseInt(parts[2].trim());
            int toZone = Integer.parseInt(parts[3].trim());
            CityRideDataset.TimeBand band = CityRideDataset.TimeBand.valueOf(parts[4].trim());
            CityRideDataset.PassengerType type = CityRideDataset.PassengerType.valueOf(parts[5].trim());
            int zonesCrossed = Integer.parseInt(parts[6].trim());
            BigDecimal base = new BigDecimal(parts[7].trim());
            BigDecimal discounted = new BigDecimal(parts[8].trim());
            BigDecimal charged = new BigDecimal(parts[9].trim());

            return new Journey(id, date, fromZone, toZone, type, band,
                    zonesCrossed, base, discounted, charged);

        } catch (Exception e)
        {
            System.out.println("Skipping malformed CSV line: " + line);
            return null;
        }
    }
}
