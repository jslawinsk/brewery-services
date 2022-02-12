package com.brewery.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith( JUnit4.class )
public class SensorDataTest {

	@Test
	public void createSensorData() throws Exception
	{
		SensorData sensorData = new SensorData( 69.0, 70.0, "AUTO", "AUTO", "ON");
		
		assertEquals( "AUTO", sensorData.getHeat() );
		assertEquals( "AUTO", sensorData.getCool() );
		assertEquals( "ON", sensorData.getControl() );
		
		sensorData.setHeat( "ON" );
		sensorData.setCool( "OFF" );
		sensorData.setControl( "OFF" );
		
		assertEquals( "ON", sensorData.getHeat() );
		assertEquals( "OFF", sensorData.getCool() );
		assertEquals( "OFF", sensorData.getControl() );

		sensorData = new SensorData( 69.0, 70.0, 1.0, 0.0, "AUTO", "AUTO", "ON", "F" );
		assertEquals( "AUTO", sensorData.getHeat() );
		assertEquals( "AUTO", sensorData.getCool() );
		assertEquals( "ON", sensorData.getControl() );
	}
}
