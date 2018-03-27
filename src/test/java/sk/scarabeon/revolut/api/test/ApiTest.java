package sk.scarabeon.revolut.api.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Assert;
import sk.scarabeon.revolut.api.ApiConfiguration;
import sk.scarabeon.revolut.api.ApiApplication;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class ApiTest {

	@ClassRule
	public static final DropwizardAppRule<ApiConfiguration> RULE = new DropwizardAppRule<ApiConfiguration>(ApiApplication.class);

	private static String baseUrl;
	private static Client client;

	@BeforeClass
	public static void setUp() {
		baseUrl = String.format("http://localhost:%d", RULE.getLocalPort());
		client = new JerseyClientBuilder(RULE.getEnvironment()).build("Revolut API Client");
	}

	/**
	 * Checks if there is an account present in memory
	 */
	@Test
	public void accountExistsTest() {
		String url = String.format("%s/revolut", baseUrl);
		Response response = client.target(url).request().accept(APPLICATION_JSON_TYPE).get();

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals("Revolut API", response.readEntity(String.class));
	}

	/**
	 * Proofs that account with given ID does not exist
	 */
	@Test
	public void accountNotExistsTest() {
		final long account = 874665L;

		String url = String.format("%s/revolut/get/" + account, baseUrl);
		Response response = client.target(url).request().accept(APPLICATION_JSON_TYPE).get();

		Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
	}

	/**
	 * Test of money deposit on account in the same currency
	 */
	@Test
	public void depositIntoAccountTest() {
		final long account = 1L;
		final double amount = 1042.33;
		final String currency = "EUR";

		String url = String.format("%s/revolut/deposit/" + account + "/" + amount + "/" + currency, baseUrl);
		Response response = client.target(url).request().get();

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals("10000.01", response.readEntity(String.class));
	}

	/**
	 * Deposit to account in different currency
	 */
	@Test
	public void depositIntoAccountWithConversionTest() {
		final long account = 2L;
		final double amount = 1000;
		final String currency = "GBP";

		String url = String.format("%s/revolut/deposit/" + account + "/" + amount + "/" + currency, baseUrl);
		Response response = client.target(url).request().get();

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals("2250.0", response.readEntity(String.class));
	}

	/**
	 * Withdraw money from account in the same currency
	 */
	@Test
	public void withdrawFromAccountTest() {
		final long account = 3L;
		final double amount = 30000.00;
		final String currency = "EUR";

		String url = String.format("%s/revolut/withdraw/" + account + "/" + amount + "/" + currency, baseUrl);
		Response response = client.target(url).request().get();

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals("1544144.28", response.readEntity(String.class));
	}

	/**
	 * Withdraw from account in different currency - withdrawal to zero
	 */
	@Test
	public void withdrawFromAccountWithConversionTest() {
		final long account = 4L;
		final double amount = 1200.00;
		final String currency = "USD";

		String url = String.format("%s/revolut/withdraw/" + account + "/" + amount + "/" + currency, baseUrl);
		Response response = client.target(url).request().get();

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals("0.0", response.readEntity(String.class));
	}

	/**
	 * Avoid to withdraw money if account's balance is insufficient
	 */
	@Test
	public void denyWithdrawal() {
		final long account = 5L;
		final double amount = 2000.00;
		final String currency = "EUR";

		String url = String.format("%s/revolut/withdraw/" + account + "/" + amount + "/" + currency, baseUrl);
		Response response = client.target(url).request().get();

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}

	/**
	 * Simple transfer between two accounts in the same currency
	 */
	@Test
	public void transferBetweenAccountsTest() {
		final long accountFrom = 6L;
		final long accountTo = 7L;
		final double amount = 25.40;
		final String currency = "EUR";
		
		String url = String.format("%s/revolut/transfer/" + accountFrom + "/" + accountTo + "/" + amount + "/" + currency, baseUrl);
		Response response = client.target(url).request().get();

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals("50.0, 38.95", response.readEntity(String.class));
	}

	/**
	 * Transfer between two account with different currencies in just other currency
	 */
	@Test
	public void transferBetweenAccountsDifferentCurrencyTest() {
		final long accountFrom = 8L;
		final long accountTo = 9L;
		final double amount = 200.00;
		final String currency = "EUR";

		String url = String.format("%s/revolut/transfer/" + accountFrom + "/" + accountTo + "/" + amount + "/" + currency, baseUrl);
		Response response = client.target(url).request().get();

		Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
		Assert.assertEquals("617.77, 376.21", response.readEntity(String.class));
	}

	/**
	 * Deny money transfer if sender's account balance is insufficient
	 */
	@Test
	public void avoidTransferTest()
	{
		final long accountFrom = 8L;
		final long accountTo = 9L;
		final double amount = 2000.00;
		final String currency = "EUR";

		String url = String.format("%s/revolut/transfer/" + accountFrom + "/" + accountTo + "/" + amount + "/" + currency, baseUrl);
		Response response = client.target(url).request().get();

		Assert.assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}
}