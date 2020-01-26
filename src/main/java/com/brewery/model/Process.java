package com.brewery.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "process", schema="brewery")
public class Process {
	
	@Id
	private String code;
	private String name;
	private boolean synched;
	
/*	
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private Set<Measurement> measurements;	

    public Process(String name, Measurement... measurements) {
        this.measurements = Stream.of(measurements).collect(Collectors.toSet());
        this.measurements.forEach(x -> x.setProcess(this));
    }    
*/
	
	public Process() {
		this.synched = false;
	}
	public Process( String code, String name) {
		super();
		this.synched = false;
		this.code = code;
		this.name = name;
	}
	public Process( boolean synched, String code, String name) {
		super();
		this.synched = synched;
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

	public boolean isSynched() {
		return synched;
	}
	public void setSynched(boolean synched) {
		this.synched = synched;
	}
	
	@Override
	public String toString() {
		return "Process [code=" + code + ", name=" + name + ", synched=" + synched + "]";
	}
}
