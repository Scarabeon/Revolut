package sk.scarabeon.revolut.api.resources;

import java.util.Currency;

public class Rates {

	/**
	 * Currency rates according to EUR (as default currency)
	 * @param currency
	 * @return
	 */
	public static double getRate(Currency currency) {
		switch (currency.getCurrencyCode()) {
		case "EUR": return 1.0;
		case "GBP": return 0.8;
		case "USD": return 1.2;
		}
		return 1.0;
	}
}