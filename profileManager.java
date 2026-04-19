import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class ProfileManager
{
    public static void saveProfile(RiderProfile profile, String filePath)
    {
        try (Writer writer = new FileWriter(filePath))
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(profile, writer);
        } catch (IOException e)
        {
            System.out.println("Error saving profile: " + e.getMessage());
        }
    }

    public static RiderProfile loadProfile(String filePath)
    {
        File file = new File(filePath);

        if (!file.exists())
        {
            return null;
        }

        try (Reader reader = new FileReader(file))
        {
            Gson gson = new Gson();
            return gson.fromJson(reader, RiderProfile.class);
        } catch (IOException e)
        {
            System.out.println("Error loading profile: " + e.getMessage());
            return null;
        }
    }
}
