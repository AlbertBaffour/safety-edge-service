package project.apt.safetyedgeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import project.apt.safetyedgeservice.model.Car;
import project.apt.safetyedgeservice.model.Inspection;
import project.apt.safetyedgeservice.model.InspectionHistory;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CarInspectionController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${inspectionservice.baseurl}")
    private String inspectionServiceBaseUrl;

    @Value("${carinfoservice.baseurl}")
    private String carInfoServiceBaseUrl;

    @GetMapping("/inspections/")
    public List<InspectionHistory> getInspections(){
        List<InspectionHistory> returnList= new ArrayList();
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {
                        });
        List<Inspection> inspections = responseEntityInspections.getBody();
        if (inspections != null) {
            for (Inspection inspection :inspections) {
                Car car = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                        Car.class, inspection.getLicensePlate());
                if (car !=null){
                    returnList.add(new InspectionHistory(car, inspection));
                }
            }
        }
        return returnList;
    }
    @GetMapping("/inspections/license_plate/{licensePlate}")
    public InspectionHistory getInspectionsByLicensePlate(@PathVariable String licensePlate){
        InspectionHistory inspectionHistory= new InspectionHistory();
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/{licensePlate}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {
                        }, licensePlate);
        List<Inspection> inspections = responseEntityInspections.getBody();
        Car car = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                                Car.class, licensePlate);
        if(car != null){
            inspectionHistory= new InspectionHistory(car,inspections);
        }
        return inspectionHistory;
    }
    @GetMapping("/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}")
    public InspectionHistory getInspectionsByLicensePlateAndInspectionDate(@PathVariable String licensePlate,@PathVariable LocalDate inspectionDate){
        InspectionHistory inspectionHistory= new InspectionHistory();
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/"+licensePlate+"/inspection_date/"+inspectionDate,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {
                        });
        List<Inspection> inspections = responseEntityInspections.getBody();
        Car car = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                Car.class, licensePlate);
        if(car != null){
            inspectionHistory= new InspectionHistory(car,inspections);
        }
        return inspectionHistory;
    }
    @GetMapping("/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}/passed/{passed}")
    public InspectionHistory getInspectionsByLicensePlateAndInspectionDateAndPassed(@PathVariable String licensePlate,@PathVariable LocalDate inspectionDate,@PathVariable Boolean passed){
        InspectionHistory inspectionHistory= new InspectionHistory();
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/"+licensePlate+"/inspection_date/"+inspectionDate+"/passed/"+passed,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {});
        List<Inspection> inspections = responseEntityInspections.getBody();
        Car car = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                Car.class, licensePlate);
        if(car != null){
            inspectionHistory= new InspectionHistory(car,inspections);
        }
        return inspectionHistory;
    }
    @PostMapping("/inspections")
    public InspectionHistory addInspection(@RequestParam String licensePlate, @RequestParam String comment, @RequestParam Boolean passed){
        Long inspectionNumber= Instant.now().getEpochSecond();
        Inspection inspection =
                restTemplate.postForObject("http://" + inspectionServiceBaseUrl + "/inspections",
                        new Inspection(inspectionNumber,licensePlate,comment,passed, LocalDate.now()),Inspection.class);

        Car car =
                restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                        Car.class,licensePlate);

        return new InspectionHistory(car, inspection);
    }

    @PutMapping("/inspections")
    public InspectionHistory updateInspection(@RequestParam Long inspectionNumber,@RequestParam String licensePlate, @RequestParam String comment, @RequestParam Boolean passed){

        Inspection inspection =
                restTemplate.getForObject("http://" +inspectionServiceBaseUrl+ "/inspections/inspection_number/" + inspectionNumber,
                        Inspection.class);
        Inspection retrievedInspection=new Inspection();
        if(inspection!=null) {
            if (comment != null) {
                inspection.setComment(comment);
            }
            if (passed != null) {
                inspection.setPassed(passed);
            }
            inspection.setInspectionDate(LocalDate.now());

            ResponseEntity<Inspection> responseEntityInspection =
                    restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections",
                            HttpMethod.PUT, new HttpEntity<>(inspection), Inspection.class);

            retrievedInspection = responseEntityInspection.getBody();
        }
        Car car =
                restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/license_plate/{licensePlate}",
                        Car.class,licensePlate);

        assert car != null;
        return new InspectionHistory(car, retrievedInspection);
    }

    @DeleteMapping("/inspections/inspection_number/{inspectionNumber}")
    public ResponseEntity deleteInspection(@PathVariable Long inspectionNumber){

        restTemplate.delete("http://" + inspectionServiceBaseUrl + "/inspections/inspection_number/" + inspectionNumber);

        return ResponseEntity.ok().build();
    }

}
