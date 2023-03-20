import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TriplePlayTest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/13/2023
 * @since 1.0.0
 */
public class TriplePlayTest {
	private final Communicator communicator=new Communicator();
	@BeforeEach()
	public void setUp() throws Exception {
		communicator.setHost("10.34.41.125");
		communicator.setTrustAllCertificates(true);
		communicator.setPort(80);
		communicator.setTrustAllCertificates(true);
		communicator.setProtocol("http");
		communicator.setContentType("application/json");
		communicator.init();
	}

	@AfterEach()
	public void destroy() throws Exception {
		communicator.disconnect();
	}

	@Test
	public void testRun() throws Exception {
		communicator.getMultipleStatistics();
		Thread.sleep(2000);
		communicator.getMultipleStatistics();
		Thread.sleep(2000);
		communicator.getMultipleStatistics();
		Thread.sleep(2000);
		communicator.getMultipleStatistics();
		Thread.sleep(2000);
		communicator.getMultipleStatistics();
		Thread.sleep(2000);
		communicator.getMultipleStatistics();
		Thread.sleep(2000);
	}
}