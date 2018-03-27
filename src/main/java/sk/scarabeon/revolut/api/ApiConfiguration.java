package sk.scarabeon.revolut.api;

import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class ApiConfiguration extends Configuration {

	@NotEmpty
	private String defaultName = "Revolut API";

	@JsonProperty
	public String getDefaultName() {
		return defaultName;
	}

	@JsonProperty
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}
}