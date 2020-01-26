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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "measurement", schema="brewery")
@SequenceGenerator(name="seq", initialValue=1, allocationSize=1)
public class Measurement {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;

	private boolean synched;
	
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
	
	//
	//	For H2 database use the following
	//
	// @Column(name = "startTime", columnDefinition="DATETIME")
	//
	//	For Postgres database use the following
	//
	@Column( name = "startTime" )
    @Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date measurementTime;

	public Measurement() {
		super();
		this.synched = false;
	}
	public Measurement( double valueNumber, String valueText, Batch batch, Process process, MeasureType type, Date measurementTime) 
	{
		super();
		this.synched = false;
		this.valueNumber = valueNumber;
		this.valueText = valueText;
		this.batch = batch;
		this.process = process;
		this.type = type;
		this.measurementTime = measurementTime;
	}
	public Measurement( boolean synched, double valueNumber, String valueText, Batch batch, Process process, MeasureType type, Date measurementTime) 
	{
		super();
		this.synched = synched;
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

	public boolean isSynched() {
		return synched;
	}

	public void setSynched(boolean synched) {
		this.synched = synched;
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
				+ batch + ", process=" + process + ", type=" + type + ", measurementTime=" + measurementTime 
				+ ", synched=" + synched
				+ "]";
	}
}
