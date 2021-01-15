package com.brewery.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DataSynchHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSynchStatus dataSynchStatus;

    private final String message_key = "Data Synch Service";
	
	@Override
	public Health health() {
		if( dataSynchStatus.up ) {
			return Health.up().withDetail( message_key, dataSynchStatus.getMessage() ).build();
		}
		else {
			return Health.down().withDetail( message_key, dataSynchStatus.getMessage() ).build();
		}
	}

}
