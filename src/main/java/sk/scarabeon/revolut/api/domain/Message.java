package sk.scarabeon.revolut.api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

	private final String message;

	public Message(@JsonProperty("message") String message) {
		this.message = message;
	}

	@JsonCreator
	public String getMessage() {
		return message;
	}
}