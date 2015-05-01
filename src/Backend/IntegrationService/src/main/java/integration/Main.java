package integration;

import integration.scheduled.UpdateProductProcessTask;
import integration.scheduled.CreateOrderProcessTask;
import org.springframework.boot.SpringApplication;

public class Main {
	public static void main(String[] args) {
		//Adding the scheduled task classes to the spring application to run in background threads.
		SpringApplication.run(new Object[] { CreateOrderProcessTask.class, UpdateProductProcessTask.class }, new String[0]);
	}
}
