package com.example.examplequerydslspringdatajpamaven.entity;

/**
 * Set token authorization in this model
 * @author fuinco
 *
 */
public class Token {
	private Long userId;
	private String token;
	private String lastUpdate;
	
	public Token(Long userId , String token ,String lastUpdate) {
		this.userId = userId;
		this.token = token;
		this.lastUpdate= lastUpdate;
		
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
}
