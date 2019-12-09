package com.brewery.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class MeasureType {
	
	@Id
	private String code;
	private String name;
	
    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private Set<Measurement> measurements;	

    public MeasureType(String name, Measurement... measurements) {
        this.measurements = Stream.of(measurements).collect(Collectors.toSet());
        this.measurements.forEach(x -> x.setType(this));
    }    
    
	public MeasureType() {
		// TODO Auto-generated constructor stub
	}

	public MeasureType(String code, String name) {
		super();
		this.code = code;
		this.name = name;
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

	@Override
	public String toString() {
		return "MeasureType [code=" + code + ", name=" + name + "]";
	}
}
