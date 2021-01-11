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
@Table(name = "sensor", schema="brewery")
public class Sensor {

	@Id
	@SequenceGenerator(name="sensorseq", initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="sensorseq")
	private Long id;

	private boolean enabled;
	
	private String name;
	private String url;
	private String userId;
	private String pin;
	private String communicationType;
	private String trigger;

	@Enumerated( EnumType.STRING )
	private DbSync dbSynch;
	
	@ManyToOne
    @JoinColumn
    private Batch batch;
	
	@ManyToOne
    @JoinColumn
	private Process process;

	@ManyToOne
    @JoinColumn
	private MeasureType measureType;
	
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
    private Date updateTime;

	public Sensor() {
		super();
		this.enabled = false;
		this.dbSynch = DbSync.ADD; 
	}
	public Sensor(Long id, boolean enabled, String name, String url, String userId, String pin, String communicationType, String trigger,
			Batch batch, Process process, MeasureType measureType, Date updateTime) {
		super();
		this.id = id;
		this.enabled = enabled;
		this.name = name;
		this.url = url;
		this.userId = userId;
		this.pin = pin;
		this.communicationType = communicationType;
		this.trigger = trigger;
		this.batch = batch;
		this.process = process;
		this.measureType = measureType;
		this.updateTime = updateTime;
		this.dbSynch = DbSync.ADD; 
	}
	public Sensor(Long id, boolean enabled, String name, String url, String userId, String pin, String communicationType, String trigger,
			Batch batch, Process process, MeasureType measureType, Date updateTime, DbSync dbSynch ) {
		super();
		this.id = id;
		this.enabled = enabled;
		this.name = name;
		this.url = url;
		this.userId = userId;
		this.pin = pin;
		this.communicationType = communicationType;
		this.trigger = trigger;
		this.batch = batch;
		this.process = process;
		this.measureType = measureType;
		this.updateTime = updateTime;
    	this.dbSynch = dbSynch;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getCommunicationType() {
		return communicationType;
	}

	public void setCommunicationType(String communicationType) {
		this.communicationType = communicationType;
	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
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

    public MeasureType getMeasureType() {
		return measureType;
	}
	public void setMeasureType(MeasureType measureType) {
		this.measureType = measureType;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

    public DbSync getDbSynch() {
		return dbSynch;
	}
	public void setDbSynch(DbSync dbSynch) {
		this.dbSynch = dbSynch;
	}
	
	@Override
	public String toString() {
		return "Sensor [id=" + id + ", enabled=" + enabled + ", name=" + name + ", url=" + url + ", userId=" + userId + ", pin=" + pin
				+ ", communicationType=" + communicationType + ", trigger=" + trigger 
				+", batch=" + batch + ", process=" + process + ", measureType=" + measureType + ", updateTime=" + updateTime 
				+ ", dbSynch=" + dbSynch
				+ "]";
	}
	
}
