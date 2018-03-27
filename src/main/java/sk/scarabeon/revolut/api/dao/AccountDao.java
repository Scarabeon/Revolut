package sk.scarabeon.revolut.api.dao;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

import sk.scarabeon.revolut.api.domain.Account;
import sk.scarabeon.revolut.api.exception.WithdrawalException;
import sk.scarabeon.revolut.api.resources.Rates;

public class AccountDao {

	private static Map<Long, Account> accounts = new HashMap<Long, Account>();
	private final static String EUR = "EUR";
	private final static String GBP = "GBP";
	private final static String USD = "USD";

	// Sample accounts
	static {
		accounts.put(1L, new Account(1L, Currency.getInstance(EUR), 8957.68));
		accounts.put(2L, new Account(2L, Currency.getInstance(EUR), 1000.00));
		accounts.put(3L, new Account(3L, Currency.getInstance(EUR), 1574144.28));
		accounts.put(4L, new Account(4L, Currency.getInstance(EUR), 1000.00));
		accounts.put(5L, new Account(5L, Currency.getInstance(EUR), 1999.99));
		accounts.put(6L, new Account(6L, Currency.getInstance(EUR), 75.40));
		accounts.put(7L, new Account(7L, Currency.getInstance(EUR), 13.55));
		accounts.put(8L, new Account(8L, Currency.getInstance(GBP), 777.77));
		accounts.put(9L, new Account(9L, Currency.getInstance(USD), 136.21));
	}

	public static Account getAccountById(long id) {
		return accounts.get(id);
	}

	public static List<Account> getAllAccounts() {
		List<Account> result = new ArrayList<Account>();
		for (Long key : accounts.keySet()) {
			result.add(accounts.get(key));
		}
		return result;
	}

	public static double depositIntoAccount(long id, double amount, String currency) {
		Currency c = Currency.getInstance(currency);
		Account account = accounts.get(id);
		if (account.getCurrency().equals(c)) {
			account.setBalance(account.getBalance() + amount);
		} else {
			double balanceInEur = account.getBalance() / Rates.getRate(account.getCurrency());
			double amountToAdd = amount / Rates.getRate(c);
			double sumInEur = balanceInEur + amountToAdd; 
			account.setBalance(sumInEur * Rates.getRate(account.getCurrency()));
		}
		return account.getBalance();
	}

	public static double withdrawFromAccount(long id, double amount, String currency) throws WithdrawalException {
		Currency c = Currency.getInstance(currency);
		Account account = accounts.get(id);
		double originalBalance = account.getBalance();
		if (account.getCurrency().equals(c)) {
			account.setBalance(account.getBalance() - amount);
			if (account.getBalance() < 0.0) {
				account.setBalance(originalBalance);
				throw new WithdrawalException("Insufficient account balance!");
			}
		}
		else {
			double balanceInEur = account.getBalance() / Rates.getRate(account.getCurrency());
			double amountToAdd = amount / Rates.getRate(c);
			double sumInEur = balanceInEur - amountToAdd; 
			account.setBalance(sumInEur * Rates.getRate(account.getCurrency()));
			if (account.getBalance() < 0.0) {
				account.setBalance(originalBalance);
				throw new WithdrawalException("Insufficient account balance!");
			}
		}
		return account.getBalance();
	}

	public static ImmutablePair<Double, Double> transferBetweenAccounts(long fromAccountId, long toAccountId, double amount, String currency) throws WithdrawalException {
		withdrawFromAccount(fromAccountId, amount, currency);
		depositIntoAccount(toAccountId, amount, currency);

		Double balanceFrom = (double) Math.round(accounts.get(fromAccountId).getBalance() * 100) / 100;
		Double balanceTo = (double) Math.round(accounts.get(toAccountId).getBalance() * 100) / 100;
		return new ImmutablePair<Double, Double>(balanceFrom, balanceTo);
	}

	public static String updateAccount(Account account) {
		String result = "";
		if (accounts.get(account.getId()) != null) {
			result = "Updated Account with id =" + account.getId();
		} else {
			result = "Added Account with id =" + account.getId();
		}
		accounts.put(account.getId(), account);
		return result;
	}
}