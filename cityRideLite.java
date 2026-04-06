import java.util.Scanner;

public class cityRideLite
{

    private static final String CONFIG_FILE = "config.json";

    public static void main(String[] args)
    {
        cityRideLite app = new cityRideLite();
        app.start();
    }

    private void start()
    {

        System.out.println("Welcome to CityRide Lite!");
        loadConfig();

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running)
        {
            printMainMenu();
            String choice = sc.nextLine().trim();

            switch (choice)
            {
                case "1":
                    RiderSession riderSession = new RiderSession();
                    riderSession.runRiderMenu(sc);
                    break;
                case "2":
                    AdminSession adminSession = new AdminSession();
                    adminSession.runAdminMenu(sc);
                    break;
                case "3":
                    running = false;
                    System.out.println("Thank you for using CityRide Lite. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }

        sc.close();
    }

    private void printMainMenu()
    {
        System.out.println("\n==============================");
        System.out.println("     CityRide Lite");
        System.out.println("==============================");
        System.out.println("1. Rider");
        System.out.println("2. Admin");
        System.out.println("3. Exit");
        System.out.print("Choose role: ");
    }

    private void loadConfig()
    {
        boolean loaded = JsonHandler.loadConfig(CONFIG_FILE);
        if (loaded) {
            System.out.println(" Configuration loaded from " + CONFIG_FILE + ".");
        } else
        {
            System.out.println(" config.json not found - using default configuration.");
        }
    }
}
