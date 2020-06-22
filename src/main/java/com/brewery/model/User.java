package com.brewery.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "user", schema="brewery")
public class User {

    @Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;

    @Column( name="username", nullable = false, unique = true )
	private String username;

    private String password;
    
	//@Enumerated( EnumType.STRING )
	//private UserRoles roles;

    private String roles;    
    
	private String token;
    
	@Enumerated( EnumType.STRING )
	private DbSync dbSynch;
    
	public User() {
		super();
		// this.roles = UserRoles.GUEST;
		this.dbSynch = DbSync.ADD; 
	}

	public User(String username, String password, String roles) {
		super();
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.dbSynch = DbSync.ADD;
	}
	
	public User(String username, String password, DbSync dbSynch, String roles) {
		super();
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.dbSynch = dbSynch;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public DbSync getDbSynch() {
		return dbSynch;
	}

	public void setDbSynch(DbSync dbSynch) {
		this.dbSynch = dbSynch;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", roles=" + roles
				+ ", token= " + token + ", dbSynch=" + dbSynch + "]";
	}
	
}
