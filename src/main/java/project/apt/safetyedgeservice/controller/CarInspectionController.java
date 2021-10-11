package project.apt.safetyedgeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import project.apt.safetyedgeservice.model.CarInfo;
import project.apt.safetyedgeservice.model.Inspection;
import project.apt.safetyedgeservice.model.InspectionHistory;

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
                CarInfo carInfo = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                        CarInfo.class, inspection.getLicensePlate());
                if (carInfo!=null){
                    returnList.add(new InspectionHistory(carInfo, inspection));
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
        CarInfo carInfo = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                                CarInfo.class, licensePlate);
        if(carInfo != null){
            inspectionHistory= new InspectionHistory(carInfo,inspections);
        }
        return inspectionHistory;
    }
    @GetMapping("/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}")
    public InspectionHistory getInspectionsByLicensePlateAndInspectionDate(@PathVariable String licensePlate,@PathVariable LocalDate inspectionDate){
        InspectionHistory inspectionHistory= new InspectionHistory();
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {
                        }, licensePlate,inspectionDate);
        List<Inspection> inspections = responseEntityInspections.getBody();
        CarInfo carInfo = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                CarInfo.class, licensePlate);
        if(carInfo != null){
            inspectionHistory= new InspectionHistory(carInfo,inspections);
        }
        return inspectionHistory;
    }
    @GetMapping("/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}/passed/{passed}")
    public InspectionHistory getInspectionsByLicensePlateAndInspectionDateAndPassed(@PathVariable String licensePlate,@PathVariable LocalDate inspectionDate,@PathVariable Boolean passed){
        InspectionHistory inspectionHistory= new InspectionHistory();
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}/passed/{passed}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {
                        }, licensePlate,inspectionDate);
        List<Inspection> inspections = responseEntityInspections.getBody();
        CarInfo carInfo = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/car/{licensePlate}",
                CarInfo.class, licensePlate);
        if(carInfo != null){
            inspectionHistory= new InspectionHistory(carInfo,inspections);
        }
        return inspectionHistory;
    }


}
