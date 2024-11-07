package se.alipsa.munin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import se.alipsa.munin.model.Report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.alipsa.munin.model.ReportType.GROOVY;
import static se.alipsa.munin.model.ReportType.R;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(ApiTest.class);

  @LocalServerPort
  protected int port;

  private final TestRestTemplate restTemplate = new TestRestTemplate();

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testAddReport() throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    Report report = new Report();
    report.setReportName("Hello Example");
    report.setDescription("Hello World");
    report.setTemplate("\"library(\"se.alipsa:htmlcreator\")\n" +
        "html.clear()\n" +
        "html.add(\"<h1>Hello World</h1>\")\n" +
        "html.content()\"");
    report.setInputContent("");
    report.setReportType(R);
    report.setReportGroup("Examples");
    String json = objectMapper.writeValueAsString(report);
    HttpEntity<String> request = new HttpEntity<>(json, headers);

    ResponseEntity<String> response = restTemplate
        .withBasicAuth("analyst", "analystpwd")
        .postForEntity("http://localhost:" + port + "/api/addReport", request, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Should be 200 OK, body was " + response.getBody());

    // Add again - should fail
    response = restTemplate
        .withBasicAuth("analyst", "analystpwd")
        .postForEntity("http://localhost:" + port + "/api/addReport", request, String.class);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Should be 400 BAD_REQUEST");
    assertEquals("There is already a report with that name", response.getBody());
  }

  @Test
  public void testUpdateReport() throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    Report report = new Report();
    report.setReportName("Hello Example2");
    report.setDescription("Hello World");
    report.setTemplate("<h1>Hello World</h1>");
    report.setInputContent("");
    report.setReportType(GROOVY);
    report.setReportGroup("Examples");
    String json = objectMapper.writeValueAsString(report);
    HttpEntity<String> request = new HttpEntity<>(json, headers);

    ResponseEntity<String> response = restTemplate
        .withBasicAuth("analyst", "analystpwd")
        .postForEntity("http://localhost:" + port + "/api/addReport", request, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode(), "Should be 200 OK, body was " + response.getBody());


    report.setDescription("Hello World2");
    json = objectMapper.writeValueAsString(report);
    request = new HttpEntity<>(json, headers);
    // update
    restTemplate
        .withBasicAuth("analyst", "analystpwd")
        .put("http://localhost:" + port + "/api/updateReport", request, String.class);

    String url = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/api/getReport")
        .queryParam("name", report.getReportName()).encode().toUriString();
    var res = restTemplate
        .withBasicAuth("analyst", "analystpwd")
        .getForEntity(url, String.class);
     LOG.debug("Got: {}", res.getBody());

     var report2 = new ObjectMapper().readValue(res.getBody(), Report.class);
    assertEquals(report.getDescription(), report2.getDescription());
  }
}
