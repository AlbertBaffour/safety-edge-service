package project.apt.safetyedgeservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import project.apt.safetyedgeservice.model.CarInfo;
import project.apt.safetyedgeservice.model.Inspection;
import project.apt.safetyedgeservice.model.InspectionHistory;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static project.apt.safetyedgeservice.model.CarInfo.PortierOptie.TWEEDEURS;
import static project.apt.safetyedgeservice.model.CarInfo.PortierOptie.VIERDEURS;

@SpringBootTest
@AutoConfigureMockMvc
class CarInspectionControllerUnitTests {

    @Value("${inspectionservice.baseurl}")
    private String inspectionServiceBaseUrl;

    @Value("${carinfoservice.baseurl}")
    private String carInfoServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;

    ObjectMapper defaultMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
    private ObjectMapper mapper =defaultMapper();

    private CarInfo carInfo1 =new CarInfo("audi","a3","1VQW871","5", TWEEDEURS);
    private CarInfo carInfo2 = new CarInfo("vw","golf5","1VCJ854","5",VIERDEURS);

    private Inspection inspection1= new Inspection(1L,"1VQW871","banden versleten",false, LocalDate.now().minusDays(2));
    private Inspection inspection2= new Inspection(2L,"1VQW871","Geen opmerking",true, LocalDate.now());
    private Inspection inspection3= new Inspection(3L,"1ABC871","koplamp stuk",false, LocalDate.now().minusDays(2));
    private Inspection inspection4= new Inspection(4L,"1ABC871","Geen opmerking",true, LocalDate.now());

    private List<Inspection> allInspections = Arrays.asList(inspection1, inspection2,inspection3,inspection4);
    private List<Inspection> allInspectionsCar1 = Arrays.asList(inspection1, inspection2);
    private List<Inspection> allInspectionsCar2 = Arrays.asList(inspection3, inspection4);
    private List<Inspection> inspectionsByPlateAndDate = Arrays.asList(inspection2);
   private List<CarInfo> allCars = Arrays.asList(carInfo1, carInfo2);


    @BeforeEach
    public void initializeMockserver() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void whenGetInspections_thenReturnInspectionsJson() throws Exception {

      // GET all inspections
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + inspectionServiceBaseUrl + "/inspections")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allInspections))
                );

        mockMvc.perform(get("/inspections"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].licensePlate", Matchers.is("1VQW871")))
                .andExpect(jsonPath("$[1].comment", Matchers.is("Geen opmerking")))
                .andExpect(jsonPath("$", hasSize(4)));
    }
    @Test
    public void whenGetInspectionsByInspectionNumber_thenReturnInspectionJson() throws Exception {
        //mock GetInspection
       mockGetInspectionByInspectionNumber("1",inspection1);

        mockMvc.perform(get("/inspections/inspection_number/{inspection_number}", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment", Matchers.is("banden versleten")))
                .andExpect(jsonPath("$.licensePlate", Matchers.is("1VQW871")))
                .andExpect(jsonPath("$.passed", Matchers.is(false)));
    }
    @Test
    public void whenGetInspectionsByLicensePlate_thenReturnInspectionsJson() throws Exception {

      // GET all inspections from Car 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/1VQW871")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allInspectionsCar1))
                );

        //mock GetCar
        mockGetCarByNumberPlate("1VQW871", carInfo1);

        mockMvc.perform(get("/inspections/license_plate/{license_plate}", "1VQW871"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", Matchers.is("a3")))
                .andExpect(jsonPath("$.licensePlate", Matchers.is("1VQW871")))
                .andExpect(jsonPath("$.merk", Matchers.is("audi")))
                .andExpect(jsonPath("$.inspections", hasSize(2)));
    }
    @Test
    public void whenGetInspectionsByLicensePlateAndDate_thenReturnInspectionHistoryJson() throws Exception {

      // GET all inspections from Car 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + inspectionServiceBaseUrl + "/inspections/license_plate/1VQW871/inspection_date/"+LocalDate.now())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(inspectionsByPlateAndDate))
                );
        //mock GetCar
        mockGetCarByNumberPlate("1VQW871",carInfo1);


        mockMvc.perform(get("/inspections/license_plate/{licensePlate}/inspection_date/{inspectionDate}", "1VQW871",LocalDate.now( )))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", Matchers.is("a3")))
                .andExpect(jsonPath("$.licensePlate", Matchers.is("1VQW871")))
                .andExpect(jsonPath("$.merk", Matchers.is("audi")))
                .andExpect(jsonPath("$.inspections", hasSize(1)));
    }
    @Test
    public void whenAddInspection_thenReturnInspectionHistoryJson() throws Exception {

        Inspection inspection= new Inspection(1L,"1VQW871","niks",true, LocalDate.now());

        // POST inspection
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + inspectionServiceBaseUrl + "/inspections")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(inspection))
                );

        //mock GetCar
        mockGetCarByNumberPlate("1VQW871", carInfo1);

        mockMvc.perform(post("/inspections")
                .param("licensePlate", inspection.getLicensePlate())
                .param("comment", inspection.getComment())
                .param("passed", inspection.getPassed().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merk", is("audi")))
                .andExpect(jsonPath("$.type", is("a3")))
                .andExpect(jsonPath("$.inspections", hasSize(1)))
                .andExpect(jsonPath("$.inspections[0].comment", is("niks")));
    }
    @Test
    public void whenUpdateInspection_thenReturnInspectionHistoryJson() throws Exception {

        Inspection inspectionUpd= new Inspection(1L,"1VQW871","in orde",true, LocalDate.now());

        //mock GetInspection
        mockGetInspectionByInspectionNumber("1",inspection1);

        // PUT inspection
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + inspectionServiceBaseUrl + "/inspections")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(inspectionUpd))
                );

        //mock GetCar
        mockGetCarByNumberPlate("1VQW871",carInfo1);

        mockMvc.perform(put("/inspections")
                .param("inspectionNumber", inspectionUpd.getInspectionNumber().toString())
                .param("licensePlate", inspectionUpd.getLicensePlate())
                .param("comment", inspectionUpd.getComment())
                .param("passed", inspectionUpd.getPassed().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merk", is("audi")))
                .andExpect(jsonPath("$.type", is("a3")))
                .andExpect(jsonPath("$.inspections[0].comment", is("in orde")))
                .andExpect(jsonPath("$.inspections[0].passed", is(true)));

    }

    @Test
    public void whenDeleteInspection_thenReturnStatusOk() throws Exception {

        // DELETE inspection
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + inspectionServiceBaseUrl + "/inspections/inspection_number/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );

        mockMvc.perform(delete("/inspections/inspection_number/{inspectionNumber}", 1L))
                .andExpect(status().isOk());
    }


    //Public methods

    // GET carinfo
    void mockGetCarByNumberPlate(String numberPlate, CarInfo carInfo) throws URISyntaxException, JsonProcessingException {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + carInfoServiceBaseUrl + "/cars/license_plate/"+numberPlate)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(carInfo))
                );
    }

    // GET inspection
     void mockGetInspectionByInspectionNumber (String inspectionNr, Inspection inspection) throws JsonProcessingException, URISyntaxException {
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + inspectionServiceBaseUrl + "/inspections/inspection_number/"+inspectionNr)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(inspection))
                );
    }
}
