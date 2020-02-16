package com.brewery.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
// @SequenceGenerator(name="seq", initialValue=1, allocationSize=1)
public class Measurement {

	@Id
	// Below is for H2 DB
	// @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	// Below is for postgres DB
	@GeneratedValue(strategy = GenerationType.AUTO, generator="measurement_id_seq")
    @SequenceGenerator(name="measurement_id_seq", sequenceName="measurement_id_seq", allocationSize=1)
    @Column(name = "id")
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

	@Enumerated( EnumType.STRING )
	private DbSync dbSynch;

	@Column( name = "startTime" )
    @Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date measurementTime;

	public Measurement() {
		super();
		this.dbSynch = DbSync.ADD; 
	}
	public Measurement( double valueNumber, String valueText, Batch batch, Process process, MeasureType type, Date measurementTime) 
	{
		super();
		this.valueNumber = valueNumber;
		this.valueText = valueText;
		this.batch = batch;
		this.process = process;
		this.type = type;
		this.measurementTime = measurementTime;
		this.dbSynch = DbSync.ADD; 
	}
	public Measurement( boolean synched, double valueNumber, String valueText, Batch batch, Process process, MeasureType type, Date measurementTime, DbSync dbSynch) 
	{
		super();
		this.valueNumber = valueNumber;
		this.valueText = valueText;
		this.batch = batch;
		this.process = process;
		this.type = type;
		this.measurementTime = measurementTime;
    	this.dbSynch = dbSynch;
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

    public DbSync getDbSynch() {
		return dbSynch;
	}
	public void setDbSynch(DbSync dbSynch) {
		this.dbSynch = dbSynch;
	}
	
	@Override
	public String toString() {
		return "Measurement [id=" + id + ", valueNumber=" + valueNumber + ", valueText=" + valueText + ", batch="
				+ batch + ", process=" + process + ", type=" + type + ", measurementTime=" + measurementTime 
				+ ", dbSynch=" + dbSynch
				+ "]";
	}
}
