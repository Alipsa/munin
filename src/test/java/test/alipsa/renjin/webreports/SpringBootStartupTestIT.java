package test.alipsa.renjin.webreports;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.alipsa.renjin.webreports.Application;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class SpringBootStartupTestIT {

  private static final Logger LOG = LoggerFactory.getLogger(SpringBootStartupTestIT.class);
  @Value("${server.port}")
  protected int port;

  @Test
  public void testStartup() {
    LOG.info("Started successfully on port {}", port);
  }
}
