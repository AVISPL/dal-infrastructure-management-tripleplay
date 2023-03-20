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
public class TriplePlayCommunicatorTest {
	private final TriplePlayCommunicator triplePlayCommunicator =new TriplePlayCommunicator();
	@BeforeEach()
	public void setUp() throws Exception {
		triplePlayCommunicator.setHost("10.34.41.125");
		triplePlayCommunicator.setTrustAllCertificates(true);
		triplePlayCommunicator.setPort(80);
		triplePlayCommunicator.setTrustAllCertificates(true);
		triplePlayCommunicator.setProtocol("http");
		triplePlayCommunicator.setContentType("application/json");
		triplePlayCommunicator.init();
	}

	@AfterEach()
	public void destroy() throws Exception {
		triplePlayCommunicator.disconnect();
	}

	@Test
	public void testRun() throws Exception {
		triplePlayCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
	}
}