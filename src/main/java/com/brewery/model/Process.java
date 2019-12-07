package com.brewery.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Process {
	
	@Id
	private String code;
	private String name;
	
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private Set<Measurement> measurements;	

    public Process(String name, Measurement... measurements) {
        this.measurements = Stream.of(measurements).collect(Collectors.toSet());
        this.measurements.forEach(x -> x.setProcess(this));
    }    
    
	public Process() {
		// TODO Auto-generated constructor stub
	}

	public Process(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}
	public void setId(String code) {
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
		return "Process [code=" + code + ", name=" + name + "]";
	}
}
