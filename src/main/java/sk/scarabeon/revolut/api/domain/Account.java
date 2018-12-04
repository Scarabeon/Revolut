package sk.scarabeon.revolut.api.domain;

import java.util.Currency;

public class Account {

	private long id;
	private Currency currency;
	private double balance;
	private String foo;

	public Account(long id, Currency currency, double balance) {
		setId(id);
		setCurrency(currency);
		setBalance(balance);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}
}