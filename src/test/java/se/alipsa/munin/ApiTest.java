package se.alipsa.munin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.alipsa.munin.model.Report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.alipsa.munin.model.ReportType.R;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest {

  //private static final Logger LOG = LoggerFactory.getLogger(ApiTest.class);

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
    report.setDefinition("\"library(\"se.alipsa:htmlcreator\")\n" +
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
}
