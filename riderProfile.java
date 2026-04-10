class RiderProfile {

    private String riderName;
    private CityRideDataset.PassengerType passengerType;
    private String defaultPayment;

    public RiderProfile(String riderName,
                        CityRideDataset.PassengerType passengerType,
                        String defaultPayment) 
    {
        this.riderName = riderName;
        this.passengerType = passengerType;
        this.defaultPayment = defaultPayment;
    }

    public String getRiderName() { return riderName; }
    public CityRideDataset.PassengerType getPassengerType() { return passengerType; }
    public String getDefaultPayment() { return defaultPayment; }

    public void setRiderName(String riderName) { this.riderName = riderName; }
    public void setPassengerType(CityRideDataset.PassengerType type) { this.passengerType = type; }
    public void setDefaultPayment(String defaultPayment) { this.defaultPayment = defaultPayment; }

    @Override
    public String toString() 
  {
        return "Rider: " + riderName
                + " | Type: " + passengerType
                + " | Payment: " + defaultPayment;
    }
}
