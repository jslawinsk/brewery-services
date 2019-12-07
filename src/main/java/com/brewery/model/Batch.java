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

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@SequenceGenerator(name="seq", initialValue=1, allocationSize=1)
public class Batch {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;
	private String name;
	private String description;
	
	@ManyToOne
    @JoinColumn
	private Style style;
	
	@Column(name = "startTime", columnDefinition="DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

	public Batch() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Batch(String name, String description, Style style, Date startTime) {
		super();
		this.name = name;
		this.description = description;
		this.style = style;
		this.startTime = startTime;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Style getStyle() {
		return style;
	}
	public void setStyle(Style style) {
		this.style = style;
	}

    public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	@Override
	public String toString() {
		return "Batch [name=" + name + ", id=" + id + ", description=" + description + ", style=" + style + ", startTime=" + startTime
				+ "]";
	}

}
