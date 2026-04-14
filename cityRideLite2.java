import java.util.Scanner;

public class CityRideLite2
{
    private static final String CONFIG_FILE = "config.json";

    public static void main(String[] args)
    {
        CityRideLite2 app = new CityRideLite2();
        app.start();
    }

    private void start()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to CityRide Lite!");

        Config config = ConfigManager.loadConfig(CONFIG_FILE);

        boolean running = true;
        while (running)
        {
            printMainMenu();
            String choice = sc.nextLine().trim();

            if (choice.equals("1"))
            {
                new RiderMenu(config).run(sc);
            } else if (choice.equals("2"))
            {
                new AdminMenu(config).run(sc);
            } else if (choice.equals("3"))
            {
                running = false;
                System.out.println("Goodbye!");
            } else
            {
                System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }

        sc.close();
    }

    private void printMainMenu()
    {
        System.out.println("\n==============================");
        System.out.println("       CityRide Lite           ");
        System.out.println("==============================");
        System.out.println("1.Rider Login");
        System.out.println("2.Admin Login");
        System.out.println("3.Exit");
        System.out.print("Choose option (1-3): ");
    }
}
