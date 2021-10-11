package project.apt.safetyedgeservice.model;

import java.util.ArrayList;
import java.util.List;

public class InspectionHistory {

    private String merk;
    private String type;
    private String licensePlate;
    private List<Inspection> inspections;

    public InspectionHistory() {
    }

    public InspectionHistory(Car car, List<Inspection> inspections) {
        setMerk(car.getMerk());
        setType(car.getType());
        setLicensePlate(car.getLicensePlate());
        setInspections(inspections);
    }

    public InspectionHistory(Car car, Inspection inspection) {
        setMerk(car.getMerk());
        setType(car.getType());
        setLicensePlate(car.getLicensePlate());
        inspections = new ArrayList<>();
        inspections.add(inspection);
        setInspections(inspections);
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

    public List<Inspection> getInspections() {
        return inspections;
    }

    public void setInspections(List<Inspection> inspections) {
        this.inspections = inspections;
    }
}
