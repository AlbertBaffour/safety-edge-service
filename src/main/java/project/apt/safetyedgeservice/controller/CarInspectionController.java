package project.apt.safetyedgeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import project.apt.safetyedgeservice.model.CarInfo;
import project.apt.safetyedgeservice.model.Inspection;
import project.apt.safetyedgeservice.model.InspectionHistory;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RestController
public class CarInspectionController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${inspectionservice.baseurl}")
    private String inspectionServiceBaseUrl;

    @Value("${carinfoservice.baseurl}")
    private String carInfoServiceBaseUrl;

    /////////////CarInfo Mapppings///////////////////////
    @GetMapping("/cars")
    public List<CarInfo> getCars(){
        ResponseEntity<List<CarInfo>> responseEntityCars =
                restTemplate.exchange("http://" + carInfoServiceBaseUrl + "/cars",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<CarInfo>>() {
                        });

        return responseEntityCars.getBody();
    }
    @GetMapping("/cars/license_plate/{licensePlate}")
    public CarInfo getCarByLicensePlate(@PathVariable String licensePlate){
        ResponseEntity<CarInfo> responseEntityCar =
                restTemplate.exchange("http://" + carInfoServiceBaseUrl + "/cars/license_plate/{licensePlate}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<CarInfo>() {
                        }, licensePlate);

        return responseEntityCar.getBody();
    }
    @GetMapping("/cars/merk/{merk}")
    public List<CarInfo> getCarsByMerk(@PathVariable String merk){
        ResponseEntity<List<CarInfo>> responseEntityCars =
                restTemplate.exchange("http://" + carInfoServiceBaseUrl + "/cars/merk/{merk}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<CarInfo>>() {
                        }, merk);

        return responseEntityCars.getBody();
    }
    @GetMapping("/cars/portier/{portier}")
    public List<CarInfo> getCarsByPortier(@PathVariable String portier){
        ResponseEntity<List<CarInfo>> responseEntityCars =
                restTemplate.exchange("http://" + carInfoServiceBaseUrl + "/cars/portier/{portier}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<CarInfo>>() {
                        }, portier);

        return responseEntityCars.getBody();
    }

    @PostMapping("/cars")
    public CarInfo addInspection(@RequestBody CarInfo carInfoNew){
        CarInfo carInfo =
                restTemplate.postForObject("http://" + carInfoServiceBaseUrl + "/cars",
                        carInfoNew,CarInfo.class);


        return carInfo;
    }

    @PutMapping("/cars")
    public CarInfo updateCarInfo(@RequestBody CarInfo carInfoUpd){
        if (!carInfoUpd.getLicensePlate().isEmpty()) {
            CarInfo carInfo =
                    restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/cars/license_plate/{licensePlate}",
                            CarInfo.class, carInfoUpd.getLicensePlate());

            if (carInfo != null) {
                if (carInfoUpd.getMerk() != null) {
                    carInfo.setMerk(carInfoUpd.getMerk());
                }
                if (carInfoUpd.getType() != null) {
                    carInfo.setType(carInfoUpd.getType());
                }
                if (carInfoUpd.getEuroNorm() != null) {
                    carInfo.setEuroNorm(carInfoUpd.getEuroNorm());
                }
                if (carInfoUpd.getPortier() != null) {
                    carInfo.setPortier(carInfoUpd.getPortier());
                }

                ResponseEntity<CarInfo> responseEntityCar =
                        restTemplate.exchange("http://" + carInfoServiceBaseUrl + "/cars",
                                HttpMethod.PUT, new HttpEntity<>(carInfo), CarInfo.class);
                return responseEntityCar.getBody();
            }

        }
        return new CarInfo();
    }
    @DeleteMapping("/cars/license_plate/{licensePlate}")
    public ResponseEntity deleteCar(@PathVariable String licensePlate){

        restTemplate.delete("http://" + carInfoServiceBaseUrl + "/cars/license_plate/{licensePlate}"  ,licensePlate);

        return ResponseEntity.ok().build();
    }

    /////////////Inspections Mapppings///////////////////////

    @GetMapping("/inspections")
    public List<Inspection> getInspections(){
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {
                        });
       return responseEntityInspections.getBody();
    }
    @GetMapping("/inspections/inspection_number/{inspectionNumber}")
    public Inspection getInspectionsByInspectionNumber(@PathVariable Long inspectionNumber){
        ResponseEntity<Inspection> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/inspection_number/{inspectionNumber}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<Inspection>() {
                        }, inspectionNumber);
        return responseEntityInspections.getBody();

    }
    @GetMapping("/inspections/license_plate/{licensePlate}")
    public InspectionHistory getInspectionsByLicensePlate(@PathVariable String licensePlate){
        InspectionHistory inspectionHistory= new InspectionHistory();
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/{licensePlate}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {
                        }, licensePlate);
        List<Inspection> inspections = responseEntityInspections.getBody();
        CarInfo carInfo = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/cars/license_plate/{licensePlate}",
                                CarInfo.class, licensePlate);
        if(carInfo != null){
            inspectionHistory= new InspectionHistory(carInfo,inspections);
        }
        return inspectionHistory;
    }
    @GetMapping("/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}")
    public InspectionHistory getInspectionsByLicensePlateAndInspectionDate(@PathVariable String licensePlate,@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inspectionDate){
        InspectionHistory inspectionHistory= new InspectionHistory();
        ResponseEntity<List<Inspection>> responseEntityInspections =
                restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Inspection>>() {
                        }, licensePlate,inspectionDate);
        List<Inspection> inspections = responseEntityInspections.getBody();
        CarInfo carInfo = restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/cars/license_plate/{licensePlate}",
                CarInfo.class, licensePlate);
        if(carInfo != null){
            inspectionHistory= new InspectionHistory(carInfo,inspections);
        }
        return inspectionHistory;
    }
    @PostMapping("/inspections")
    public InspectionHistory addInspection(@RequestBody Inspection inspectionNew){
        Long inspectionNumber= Instant.now().getEpochSecond();
        inspectionNew.setInspectionNumber(inspectionNumber);
        inspectionNew.setInspectionDate(LocalDate.now());
        Inspection inspection =
                restTemplate.postForObject("http://" + inspectionServiceBaseUrl + "/inspections",
                        inspectionNew,Inspection.class);

        CarInfo carInfo =
                restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/cars/license_plate/{licensePlate}",
                        CarInfo.class,inspectionNew.getLicensePlate());

        assert carInfo != null;
        return new InspectionHistory(carInfo, inspection);
    }

    @PutMapping("/inspections")
    public InspectionHistory updateInspection(@RequestBody Inspection inspectionUpd){

        Inspection inspection =
                restTemplate.getForObject("http://" +inspectionServiceBaseUrl+ "/inspections/inspection_number/{inspectionNumber}" ,
                        Inspection.class,inspectionUpd.getInspectionNumber());
        Inspection retrievedInspection=new Inspection();
        if(inspection!=null) {
            if (inspectionUpd.getComment() != null) {
                inspection.setComment(inspectionUpd.getComment());
            }
            if (inspectionUpd.getPassed() != null) {
                inspection.setPassed(inspectionUpd.getPassed());
            }
            inspection.setInspectionDate(LocalDate.now());

            ResponseEntity<Inspection> responseEntityInspection =
                    restTemplate.exchange("http://" + inspectionServiceBaseUrl + "/inspections",
                            HttpMethod.PUT, new HttpEntity<>(inspection), Inspection.class);

            retrievedInspection = responseEntityInspection.getBody();
        }
        CarInfo carInfo =
                restTemplate.getForObject("http://" + carInfoServiceBaseUrl + "/cars/license_plate/{licensePlate}",
                        CarInfo.class,inspectionUpd.getLicensePlate());

        assert carInfo != null;
        return new InspectionHistory(carInfo, retrievedInspection);
    }
    @DeleteMapping("/inspections/inspection_number/{inspectionNumber}")
    public ResponseEntity deleteInspection(@PathVariable Long inspectionNumber){

        restTemplate.delete("http://" + inspectionServiceBaseUrl + "/inspections/inspection_number/{inspectionNumber}" , inspectionNumber);

        return ResponseEntity.ok().build();
    }


}
