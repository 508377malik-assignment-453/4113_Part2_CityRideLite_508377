import java.util.ArrayList;
import java.util.List;

public class RiderProfile
{
    private String name;
    private CityRideDataset.PassengerType passengerType;
    private String defaultPayment;
    private List<Journey> journeys;

    public RiderProfile(String name, CityRideDataset.PassengerType passengerType,
                        String defaultPayment)
    {
        this.name = name;
        this.passengerType = passengerType;
        this.defaultPayment = defaultPayment;
        this.journeys = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public CityRideDataset.PassengerType getPassengerType() { return passengerType; }
    public String getDefaultPayment() { return defaultPayment; }
    public List<Journey> getJourneys() { return journeys; }
    public void setJourneys(List<Journey> journeys) { this.journeys = journeys; }
}

