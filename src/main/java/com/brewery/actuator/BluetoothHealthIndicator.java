package com.brewery.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BluetoothHealthIndicator implements HealthIndicator {

    @Autowired
    private BluetoothStatus bluetoothStatus;

    private final String message_key = "Bluetooth Service";
	
	@Override
	public Health health() {
		// TODO Auto-generated method stub
		if( bluetoothStatus.up ) {
			return Health.up().withDetail( message_key, bluetoothStatus.getMessage() ).build();
		}
		else {
			return Health.down().withDetail( message_key, bluetoothStatus.getMessage() ).build();
		}
	}

}
