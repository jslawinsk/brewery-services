package com.brewery.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "style", schema="brewery")
public class Style {
	
	@Id
	@SequenceGenerator(name="styleseq", initialValue=1, allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="styleseq")
	private Long id;
	
	@Column(name="name", nullable=false, unique=true)
	private String name;
	
	private String description;
	private String bjcpCategory;
	
	@Enumerated( EnumType.STRING )
	private DbSync dbSynch;
	private String dbSynchToken;
	
	public Style() {
		super();
		this.dbSynch = DbSync.ADD; 
	}
    public Style( String name, String bjcpCategory, String description) {
    	this.name = name;
    	this.bjcpCategory = bjcpCategory;
    	this.description = description;
		this.dbSynch = DbSync.ADD; 
    }
    public Style( String name, String bjcpCategory, String description, DbSync dbSynch, String dbSynchToken ) {
    	this.name = name;
    	this.bjcpCategory = bjcpCategory;
    	this.description = description;
    	this.dbSynch = dbSynch;
    	this.dbSynchToken = dbSynchToken;
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
	public String getBjcpCategory() {
		return bjcpCategory;
	}
	public void setBjcpCategory(String bjcpCategory) {
		this.bjcpCategory = bjcpCategory;
	}

    public DbSync getDbSynch() {
		return dbSynch;
	}
	public void setDbSynch(DbSync dbSynch) {
		this.dbSynch = dbSynch;
	}

	public String getDbSynchToken() {
		return dbSynchToken;
	}
	public void setDbSynchToken(String dbSynchToken) {
		this.dbSynchToken = dbSynchToken;
	}
	
	@Override
	public String toString() {
		return "Style [id=" + id + ", name=" + name + ", description=" + description + ", bjcpCategory=" + bjcpCategory 
				+ ", dbSynch=" + dbSynch + ", dbSynchToken=" + dbSynchToken
				+ "]";
	}
	
}
