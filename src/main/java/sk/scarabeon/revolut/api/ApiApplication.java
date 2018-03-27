package sk.scarabeon.revolut.api;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import sk.scarabeon.revolut.api.resources.AccountResource;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

public class ApiApplication extends Application<ApiConfiguration> {

	public static void main(String[] args) throws Exception {
		new ApiApplication().run(args);
	}

	@Override
	public void run(ApiConfiguration configuration, Environment environment) throws Exception {
		environment.getObjectMapper().setPropertyNamingStrategy(SNAKE_CASE);
		environment.jersey().register(new AccountResource());
	}
}