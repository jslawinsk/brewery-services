package com.brewery.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class WiFiHealthIndicator implements HealthIndicator {

    @Autowired
    private WiFiStatus wiFiStatus;

    private final String message_key = "WiFi Service";
	
	@Override
	public Health health() {
		// TODO Auto-generated method stub
		if( wiFiStatus.up ) {
			return Health.up().withDetail( message_key, wiFiStatus.getMessage() ).build();
		}
		else {
			return Health.down().withDetail( message_key, wiFiStatus.getMessage() ).build();
		}
	}

}
