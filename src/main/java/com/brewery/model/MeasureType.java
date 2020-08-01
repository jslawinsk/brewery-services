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
	private boolean voiceAssist;
	private boolean graphData;
	private int minValue;
	private int maxValue;
	@Enumerated( EnumType.STRING )
	private GraphTypes graphType;

	@Enumerated( EnumType.STRING )
	private DbSync dbSynch;

	public MeasureType() {
		super();
		this.voiceAssist = false;
		this.graphData = false;
		this.minValue = 0;
		this.maxValue = 200;
		this.graphType = GraphTypes.NONE;
		this.dbSynch = DbSync.ADD; 
	}
	public MeasureType( String code, String name) {
		super();
		this.code = code;
		this.name = name;
		this.voiceAssist = false;
		this.graphData = false;
		this.minValue = 0;
		this.maxValue = 200;
		this.graphType = GraphTypes.NONE;
		this.dbSynch = DbSync.ADD; 
	}
	public MeasureType( String code, String name, DbSync dbSynch ) {
		super();
		this.code = code;
		this.name = name;
		this.voiceAssist = false;
		this.graphData = false;
		this.minValue = 0;
		this.maxValue = 200;
		this.graphType = GraphTypes.NONE;
    	this.dbSynch = dbSynch;
	}
	public MeasureType(String code, String name, boolean graphData, int minValue, int maxValue,
			GraphTypes graphType, DbSync dbSynch) {
		super();
		this.code = code;
		this.name = name;
		this.voiceAssist = false;
		this.graphData = graphData;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.graphType = graphType;
		this.dbSynch = dbSynch;
	}
	public MeasureType(String code, String name, boolean voiceAssist, boolean graphData, int minValue, int maxValue,
			GraphTypes graphType, DbSync dbSynch) {
		super();
		this.code = code;
		this.name = name;
		this.voiceAssist = voiceAssist;
		this.graphData = graphData;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.graphType = graphType;
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

    public boolean isVoiceAssist() {
		return voiceAssist;
	}
	public void setVoiceAssist(boolean voiceAssist) {
		this.voiceAssist = voiceAssist;
	}
	
    public boolean isGraphData() {
		return graphData;
	}
	public void setGraphData(boolean graphData) {
		this.graphData = graphData;
	}
	
	public int getMinValue() {
		return minValue;
	}
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	
	public GraphTypes getGraphType() {
		return graphType;
	}
	public void setGraphType(GraphTypes graphType) {
		this.graphType = graphType;
	}

	public DbSync getDbSynch() {
		return dbSynch;
	}
	public void setDbSynch(DbSync dbSynch) {
		this.dbSynch = dbSynch;
	}
	
	@Override
	public String toString() {
		return "MeasureType [code=" + code + ", name=" + name + ", voiceAssist=" + voiceAssist+ ", graphData=" + graphData 
				+ ", minValue=" + minValue + ", maxValue=" + maxValue + ", graphType=" + graphType + ", dbSynch=" + dbSynch + "]";
	}
	
}
