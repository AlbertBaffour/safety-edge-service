package project.apt.safetyedgeservice.model;

public class CarInfo {
    public enum PortierOptie {
        TWEEDEURS,
        VIERDEURS;
    }

    private Integer id;

    private String merk;
    private String type;
    private String licensePlate;
    private String euroNorm;
    private PortierOptie portier;

    public CarInfo() {}

    public CarInfo(String merk, String type, String licensePlate, String euroNorm, PortierOptie portier) {
        this.merk = merk;
        this.type = type;
        this.licensePlate = licensePlate;
        this.euroNorm = euroNorm;
        this.portier = portier;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getEuroNorm() {
        return euroNorm;
    }

    public void setEuroNorm(String euroNorm) {
        this.euroNorm = euroNorm;
    }

    public PortierOptie getPortier() {
        return portier;
    }

    public void setPortier(PortierOptie portier) {
        this.portier = portier;
    }

}
