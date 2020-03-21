package com.brewery.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "measureType", schema="brewery")
public class MeasureType {
	
	@Id
	private String code;
	private String name;

	@Enumerated( EnumType.STRING )
	private DbSync dbSynch;

	public MeasureType() {
		super();
		this.dbSynch = DbSync.ADD; 
	}
	public MeasureType( String code, String name) {
		super();
		this.code = code;
		this.name = name;
		this.dbSynch = DbSync.ADD; 
	}
	public MeasureType( String code, String name, DbSync dbSynch ) {
		super();
		this.code = code;
		this.name = name;
    	this.dbSynch = dbSynch;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    public DbSync getDbSynch() {
		return dbSynch;
	}
	public void setDbSynch(DbSync dbSynch) {
		this.dbSynch = dbSynch;
	}
	
	@Override
	public String toString() {
		return "MeasureType [code=" + code + ", name=" + name + ", dbSynch=" + dbSynch + "]";
	}
}
