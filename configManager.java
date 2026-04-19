import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.util.Map;

public class ConfigManager
{
    public static Config loadConfig(String filePath)
    {
        File file = new File(filePath);

        if (!file.exists())
        {
            System.out.println("Config file not found. Using default values.");
            return buildDefaultConfig();
        }

        try (Reader reader = new FileReader(file))
        {
            Gson gson = new Gson();
            Config config = gson.fromJson(reader, Config.class);
            System.out.println("Config loaded from " + filePath);
            return config;
        } catch (IOException e)
        {
            System.out.println("Error reading config: " + e.getMessage() + ". Using defaults.");
            return buildDefaultConfig();
        }
    }

    public static void saveConfig(Config config, String filePath)
    {
        try (Writer writer = new FileWriter(filePath))
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(config, writer);
        } catch (IOException e)
        {
            System.out.println("Error saving config: " + e.getMessage());
        }
    }

    private static Config buildDefaultConfig()
    {
        Config config = new Config();

        for (Map.Entry<String, BigDecimal> entry : CityRideDataset.BASE_FARE.entrySet())
        {
            config.getBaseFaresMap().put(entry.getKey(), entry.getValue().toPlainString());
        }

        for (Map.Entry<CityRideDataset.PassengerType, BigDecimal> entry : CityRideDataset.DISCOUNT_RATE.entrySet())
        {
            config.getDiscountRatesMap().put(entry.getKey().name(), entry.getValue().toPlainString());
        }

        for (Map.Entry<CityRideDataset.PassengerType, BigDecimal> entry : CityRideDataset.DAILY_CAP.entrySet())
        {
            config.getDailyCapsMap().put(entry.getKey().name(), entry.getValue().toPlainString());
        }

        return config;
    }
}
