import java.math.BigDecimal;
import java.util.Scanner;

public class InputHelper
{
    public String getDate(Scanner sc)
    {
        while (true)
        {
            System.out.print("Enter date (dd/MM/yyyy, e.g. 10/04/2026): ");
            String input = sc.nextLine().trim();

            if (input.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return input;
            }

            System.out.println("Invalid date. Please use dd/MM/yyyy format.");
        }
    }

    public int getZone(Scanner sc, String message)
    {
        while (true)
        {
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
                System.out.println("Please enter a valid number (1-5).");
            }
        }
    }

    public CityRideDataset.PassengerType getPassengerType(Scanner sc)
    {
        while (true)
        {
            System.out.println("Passenger type:");
            System.out.println("  1.Adult");
            System.out.println("  2.Student");
            System.out.println("  3.Child");
            System.out.println("  4.Senior Citizen");
            System.out.print("Choose (1-4): ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) return CityRideDataset.PassengerType.ADULT;
            if (choice.equals("2")) return CityRideDataset.PassengerType.STUDENT;
            if (choice.equals("3")) return CityRideDataset.PassengerType.CHILD;
            if (choice.equals("4")) return CityRideDataset.PassengerType.SENIOR_CITIZEN;

            System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
        }
    }

    public CityRideDataset.TimeBand getTimeBand(Scanner sc)
    {
        while (true)
        {
            System.out.print("Time band (1 = Peak, 2 = Off-peak): ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) return CityRideDataset.TimeBand.PEAK;
            if (choice.equals("2")) return CityRideDataset.TimeBand.OFF_PEAK;

            System.out.println("Invalid choice. Please enter 1 or 2.");
        }
    }

    public int parseId(Scanner sc)
    {
        try
        {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e)
        {
            System.out.println("Invalid input. Please enter a numeric ID.");
            return -1;
        }
    }

    public BigDecimal parsePositiveDecimal(String input)
    {
        try
        {
            BigDecimal value = new BigDecimal(input);
            if (value.compareTo(BigDecimal.ZERO) <= 0) return null;
            return value;
        } catch (NumberFormatException e)
        {
            return null;
        }
    }
}

