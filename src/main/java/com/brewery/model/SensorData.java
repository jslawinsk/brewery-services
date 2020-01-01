package com.brewery.model;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SensorData {

	private double temperature;
	private double target;
	private String heat;
	private String cool;

	public SensorData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SensorData(double temperature, double target, String heat, String cool) {
		super();
		this.temperature = temperature;
		this.target = target;
		this.heat = heat;
		this.cool = cool;
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

	@Override
	public String toString() {
		return "SensorData [temperature=" + temperature + ", target=" + target + ", heat=" + heat + ", cool=" + cool
				+ "]";
	}

}
