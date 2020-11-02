package se.alipsa.renjin.webreports;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringBootStartupTest {

  private static final Logger LOG = LoggerFactory.getLogger(SpringBootStartupTest.class);
  @Value("${server.port}")
  protected int port;

  @Test
  public void testStartup() {
    LOG.info("Started successfully on port {}", port);
  }
}
