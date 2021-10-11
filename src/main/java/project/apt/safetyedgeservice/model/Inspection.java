package project.apt.safetyedgeservice.model;

import java.time.LocalDate;

public class Inspection {


    private String id;
    private Integer inspectionNumber;
    private String licensePlate;
    private String comment;
    private Boolean passed;
    private LocalDate inspectionDate;

    public Inspection() {
    }

    public Inspection(String licensePlate, String comment, Boolean passed, LocalDate inspectionDate) {
        setLicensePlate(licensePlate);
        setComment(comment);
        setPassed(passed);
        setInspectionDate(inspectionDate);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed =passed;
    }

    public LocalDate getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDate inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public Integer getInspectionNumber() {
        return inspectionNumber;
    }

    public void setInspectionNumber(Integer inspectionNumber) {
        this.inspectionNumber = inspectionNumber;
    }

}
