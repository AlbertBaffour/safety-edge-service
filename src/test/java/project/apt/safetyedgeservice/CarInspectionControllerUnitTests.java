package project.apt.safetyedgeservice;

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

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private List<Inspection> allInspectionsCar1 = Arrays.asList(inspection1, inspection2);
    private List<Inspection> allInspectionsCar2 = Arrays.asList(inspection3, inspection4);
   private List<CarInfo> allCars = Arrays.asList(carInfo1, carInfo2);

    @BeforeEach
    public void initializeMockserver() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
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

        // GET Car 1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + carInfoServiceBaseUrl + "/cars/license_plate/1VQW871")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(carInfo1))
                );

        mockMvc.perform(get("/inspections/license_plate/{license_plate}", "1VQW871"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", Matchers.is("a3")))
                .andExpect(jsonPath("$.licensePlate", Matchers.is("1VQW871")))
                .andExpect(jsonPath("$.merk", Matchers.is("audi")))
                .andExpect(jsonPath("$.inspections", hasSize(2)));
    }

    /*
    @Test
    public void whenGetRankingsByTitle_thenReturnFilledBookReviewsJson() throws Exception {

        // GET Books by Title 'Book'
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + bookInfoServiceBaseUrl + "/books/title/Book")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allBooks))
                );

        // GET all reviews for Book 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/ISBN1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allReviewsForBook1))
                );

        // GET all reviews for Book 2
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/ISBN2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allReviewsForBook2))
                );

        mockMvc.perform(get("/rankings/book/title/{title}", "Book"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookTitle", is("Book1")))
                .andExpect(jsonPath("$[0].isbn", is("ISBN1")))
                .andExpect(jsonPath("$[0].userScores[0].userId", is(1)))
                .andExpect(jsonPath("$[0].userScores[0].scoreNumber", is(1)))
                .andExpect(jsonPath("$[0].userScores[1].userId", is(2)))
                .andExpect(jsonPath("$[0].userScores[1].scoreNumber", is(2)))
                .andExpect(jsonPath("$[1].bookTitle", is("Book2")))
                .andExpect(jsonPath("$[1].isbn", is("ISBN2")))
                .andExpect(jsonPath("$[1].userScores[0].userId", is(1)))
                .andExpect(jsonPath("$[1].userScores[0].scoreNumber", is(2)));

    }

    @Test
    public void whenGetRankingsByISBN_thenReturnFilledBookReviewsJson() throws Exception {

        // GET Book 1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + bookInfoServiceBaseUrl + "/books/ISBN1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(book1))
                );


        // GET all reviews for Book 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/ISBN1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allReviewsForBook1))
                );


        mockMvc.perform(get("/rankings/book/{ISBN}", "ISBN1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle", is("Book1")))
                .andExpect(jsonPath("$.isbn", is("ISBN1")))
                .andExpect(jsonPath("$.userScores[0].userId", is(1)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(1)))
                .andExpect(jsonPath("$.userScores[1].userId", is(2)))
                .andExpect(jsonPath("$.userScores[1].scoreNumber", is(2)));
    }

    @Test
    public void whenGetRankingsByUserIdAndISBN_thenReturnFilledBookReviewsJson() throws Exception {

        // GET Book 1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + bookInfoServiceBaseUrl + "/books/ISBN1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(book1))
                );

        // GET review from User 1 of Book 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/user/1/book/ISBN1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(reviewUser1Book1))
                );

        mockMvc.perform(get("/rankings/{userId}/book/{ISBN}", 1, "ISBN1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle", is("Book1")))
                .andExpect(jsonPath("$.isbn", is("ISBN1")))
                .andExpect(jsonPath("$.userScores[0].userId", is(1)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(1)));
    }

    @Test
    public void whenAddRanking_thenReturnFilledBookReviewJson() throws Exception {

        Review reviewUser3Book1 = new Review(3, "ISBN1", 3);

        // POST review for Book 1 from User 3
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(reviewUser3Book1))
                );

        // GET Book 1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + bookInfoServiceBaseUrl + "/books/ISBN1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(book1))
                );

        mockMvc.perform(post("/rankings")
                .param("userId", reviewUser3Book1.getUserId().toString())
                .param("ISBN", reviewUser3Book1.getISBN())
                .param("score", reviewUser3Book1.getScoreNumber().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle", is("Book1")))
                .andExpect(jsonPath("$.isbn", is("ISBN1")))
                .andExpect(jsonPath("$.userScores[0].userId", is(3)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(3)));
    }

    @Test
    public void whenUpdateRanking_thenReturnFilledBookReviewJson() throws Exception {

        Review updatedReviewUser1Book1 = new Review(1, "ISBN1", 5);

        // GET review from User 1 of Book 1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/user/1/book/ISBN1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(reviewUser1Book1))
                );

        // PUT review from User 1 for Book 1 with new score 5
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(updatedReviewUser1Book1))
                );

        // GET Book 1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + bookInfoServiceBaseUrl + "/books/ISBN1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(book1))
                );

        mockMvc.perform(put("/rankings")
                .param("userId", updatedReviewUser1Book1.getUserId().toString())
                .param("ISBN", updatedReviewUser1Book1.getISBN())
                .param("score", updatedReviewUser1Book1.getScoreNumber().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookTitle", is("Book1")))
                .andExpect(jsonPath("$.isbn", is("ISBN1")))
                .andExpect(jsonPath("$.userScores[0].userId", is(1)))
                .andExpect(jsonPath("$.userScores[0].scoreNumber", is(5)));

    }

    @Test
    public void whenDeleteRanking_thenReturnStatusOk() throws Exception {

        // DELETE review from User 999 of Book with ISBN9 as ISBN
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + reviewServiceBaseUrl + "/reviews/user/999/book/ISBN9")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );

        mockMvc.perform(delete("/rankings/{userId}/book/{ISBN}", 999, "ISBN9"))
                .andExpect(status().isOk());
    }

*/
}
