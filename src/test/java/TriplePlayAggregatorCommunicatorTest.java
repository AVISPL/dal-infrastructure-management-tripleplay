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
public class TriplePlayAggregatorCommunicatorTest {
	private final TriplePlayAggregatorCommunicator triplePlayAggregatorCommunicator =new TriplePlayAggregatorCommunicator();
	@BeforeEach()
	public void setUp() throws Exception {
		triplePlayAggregatorCommunicator.setHost("10.34.41.125");
		triplePlayAggregatorCommunicator.setTrustAllCertificates(true);
		triplePlayAggregatorCommunicator.setPort(80);
		triplePlayAggregatorCommunicator.setTrustAllCertificates(true);
		triplePlayAggregatorCommunicator.setProtocol("http");
		triplePlayAggregatorCommunicator.setContentType("application/json");
		triplePlayAggregatorCommunicator.init();
	}

	@AfterEach()
	public void destroy() throws Exception {
		triplePlayAggregatorCommunicator.disconnect();
	}

	@Test
	public void testRun() throws Exception {
		triplePlayAggregatorCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayAggregatorCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayAggregatorCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayAggregatorCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayAggregatorCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
		triplePlayAggregatorCommunicator.getMultipleStatistics();
		Thread.sleep(2000);
	}
}