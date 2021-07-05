package com.brewery.model;

public class SensorData {

	// Sample WiFi Data: {"temperature":78.80, "target":70, "heat":"ON", "cool":"OFF", "control":"Heat", "deviation":1.00, "calibration":0.00, "units":"F"}";
	
	private double temperature;
	private double target;
	private double deviation;
	private double calibration;
	private String heat;
	private String cool;
	private String control;
	private String units;

	public SensorData() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public SensorData(double temperature, double target, double deviation, double calibration, String heat, String cool, String control, String units) {
		super();
		this.temperature = temperature;
		this.target = target;
		this.deviation = deviation;
		this.calibration = calibration;
		this.heat = heat;
		this.cool = cool;
		this.control = control;
		this.units = units;
	}

	public SensorData(double temperature, double target, String heat, String cool, String control) {
		super();
		this.temperature = temperature;
		this.target = target;
		this.heat = heat;
		this.cool = cool;
		this.control = control;
		this.deviation = 0.0;
		this.calibration = 0.0;
		this.units = "F";
	}
	
	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getTarget() {
		return target;
	}

	public void setTarget(double target) {
		this.target = target;
	}

	public double getDeviation() {
		return deviation;
	}

	public void setDeviation(double deviation) {
		this.deviation = deviation;
	}

	public double getCalibration() {
		return calibration;
	}

	public void setCalibration(double calibration) {
		this.calibration = calibration;
	}

	public String getHeat() {
		return heat;
	}

	public void setHeat(String heat) {
		this.heat = heat;
	}

	public String getCool() {
		return cool;
	}

	public void setCool(String cool) {
		this.cool = cool;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	@Override
	public String toString() {
		return "SensorData [temperature=" + temperature + ", target=" + target + ", deviation=" + deviation
				+ ", calibration=" + calibration + ", heat=" + heat + ", cool=" + cool + ", control=" + control
				+ ", units=" + units + "]";
	}
	
}
