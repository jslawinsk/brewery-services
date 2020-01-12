package com.brewery.model;

public class SensorData {

	private double temperature;
	private double target;
	private String heat;
	private String cool;
	private String control;

	public SensorData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SensorData(double temperature, double target, String heat, String cool, String control) {
		super();
		this.temperature = temperature;
		this.target = target;
		this.heat = heat;
		this.cool = cool;
		this.control = control;
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

	@Override
	public String toString() {
		return "SensorData [temperature=" + temperature + ", target=" + target + ", heat=" + heat + ", cool=" + cool + ", control=" + control
				+ "]";
	}

}
