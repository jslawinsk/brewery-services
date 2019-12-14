package com.brewery.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@SequenceGenerator(name="seq", initialValue=1, allocationSize=1)
public class Measurement {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;
	
	private double valueNumber;
	private String valueText;
	@ManyToOne
    @JoinColumn
    private Batch batch;
	
	@ManyToOne
    @JoinColumn
	private Process process;

	@ManyToOne
    @JoinColumn
	private MeasureType type;
	
	@Column(name = "measurementTime", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date measurementTime;

	public Measurement() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Measurement(double valueNumber, String valueText, Batch batch, Process process, MeasureType type, Date measurementTime) 
	{
		super();
		this.valueNumber = valueNumber;
		this.valueText = valueText;
		this.batch = batch;
		this.process = process;
		this.type = type;
		this.measurementTime = measurementTime;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public double getValueNumber() {
		return valueNumber;
	}
	public void setValueNumber(double valueNumber) {
		this.valueNumber = valueNumber;
	}
	
	public String getValueText() {
		return valueText;
	}
	public void setValueText(String valueText) {
		this.valueText = valueText;
	}
	
	public Batch getBatch() {
		return batch;
	}
	public void setBatch(Batch batch) {
		this.batch = batch;
	}
	public Process getProcess() {
		return process;
	}
	public void setProcess(Process process) {
		this.process = process;
	}

    public MeasureType getType() {
		return type;
	}
	public void setType(MeasureType type) {
		this.type = type;
	}
	
	public Date getMeasurementTime() {
		return measurementTime;
	}
	public void setMeasurementTime(Date measurementTime) {
		this.measurementTime = measurementTime;
	}
	
	@Override
	public String toString() {
		return "Measurement [id=" + id + ", valueNumber=" + valueNumber + ", valueText=" + valueText + ", batch="
				+ batch + ", process=" + process + ", type=" + type + ", measurementTime=" + measurementTime + "]";
	}
}
