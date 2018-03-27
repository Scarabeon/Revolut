package sk.scarabeon.revolut.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.tuple.ImmutablePair;

import sk.scarabeon.revolut.api.dao.AccountDao;
import sk.scarabeon.revolut.api.domain.Account;
import sk.scarabeon.revolut.api.exception.WithdrawalException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

@Path("/revolut")
public class AccountResource {

	private static final String MESSAGE = "Revolut API";

	@GET
	@Produces(APPLICATION_JSON)
	public String setup() {
		return new String(MESSAGE);
	}

	@GET
	@Path("/get/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account getAccount(@PathParam("id") long id) throws Exception {
		try {
			if (AccountDao.getAccountById(id) != null) {
				return AccountDao.getAccountById(id);
			} else {
				throw new Exception("Account ID not found");
			}
		} catch (Exception ex) {

		}
		return null;
	}

	@GET
	@Path("/get-all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> getAccounts() {
		return AccountDao.getAllAccounts();
	}

	@GET
	@Path("/deposit/{id}/{amount}/{currency}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response depositIntoAccount(@PathParam("id") long id, @PathParam("amount") double amount, @PathParam("currency") String currency) {
		double balance = AccountDao.depositIntoAccount(id, amount, currency);
		return Response.ok(balance).build();
	}

	@GET
	@Path("/withdraw/{id}/{amount}/{currency}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response withdrawFromAccount(@PathParam("id") long id, @PathParam("amount") double amount, @PathParam("currency") String currency) {
		try {
			double balance = AccountDao.withdrawFromAccount(id, amount, currency);
			return Response.ok(balance).build();
		} catch (WithdrawalException e) {
			return Response.status(Status.FORBIDDEN).build();
		}
	}

	@GET
	@Path("/transfer/{fromId}/{toId}/{amount}/{currency}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response transferBetweenAccounts(@PathParam("fromId") long fromId, @PathParam("toId") long toId, @PathParam("amount") double amount, @PathParam("currency") String currency) {
		try {
			ImmutablePair<Double, Double> balance = AccountDao.transferBetweenAccounts(fromId, toId, amount, currency);
			return Response.ok(balance.getLeft() + ", " + balance.getRight()).build();
		} catch (WithdrawalException e) {
			return Response.status(Status.FORBIDDEN).build();
		}
	}

	@POST
	@Path("/update")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_JSON })
	public String updateAccount(Account account) {
		return AccountDao.updateAccount(account);
	}
}