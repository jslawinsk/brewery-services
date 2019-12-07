package com.brewery.model;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name="seq", initialValue=1, allocationSize=100)
public class Style {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;
	private String name;
	private String description;
	private String bjcpCategory;
	
	@OneToMany(mappedBy = "style", cascade = CascadeType.ALL)
    private Set<Batch> batches;	

    public Style( String name, String bjcpCategory, String description) {
    	this.name = name;
    	this.bjcpCategory = bjcpCategory;
    	this.description = description;
    }
    
    public Style(String name, Batch... batches) {
        this.name = name;
        this.batches = Stream.of(batches).collect(Collectors.toSet());
        this.batches.forEach(x -> x.setStyle(this));
    }    
    
	public Style() {
		// TODO Auto-generated constructor stub
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

    @Override
	public String toString() {
		return "Style [id=" + id + ", name=" + name + ", description=" + description + ", bjcpCategory=" + bjcpCategory
				+ ", batches=" + batches + "]";
	}
	
}
